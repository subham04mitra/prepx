package com.exam.Service;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.Entity.DailyQs;
import com.exam.Entity.InterviewFeedback;
import com.exam.Entity.Leaderboard;
import com.exam.Entity.MasSubscription;
import com.exam.Entity.MasUser;
import com.exam.Entity.MasUserToken;
import com.exam.Entity.TodayQs;
import com.exam.Entity.UserFeedback;
import com.exam.Entity.UserSubmission;
import com.exam.Entity.UserSubscription;
import com.exam.Exception.GlobalExceptionHandler;
import com.exam.Repositry.DailyQsRepositry;
import com.exam.Repositry.InterviewFeedbackRepository;
import com.exam.Repositry.LeaderboardRepository;
import com.exam.Repositry.MasSubscriptionRepository;
import com.exam.Repositry.MasUserRepository;
import com.exam.Repositry.MasUserTokenRepository;
import com.exam.Repositry.TodayQsRepository;
import com.exam.Repositry.UserFeedbackRepository;
import com.exam.Repositry.UserSubmissionRepository;
import com.exam.Repositry.UserSubscriptionRepository;
import com.exam.Response.ApiResponses;
import com.exam.Response.ResponseBean;
import com.exam.Security.TokenService;
import com.exam.reqDTO.CommonReqModel;
import com.exam.resDTO.DailyQsDTO;
import com.exam.resDTO.LeaderboardDTO;
import com.exam.resDTO.LoginResModel;
import com.exam.resDTO.ProfileDTO;

@Service
public class AuthServiceNew {

    @Autowired
    MasUserRepository userRepo;
    
    @Autowired
    UserSubscriptionRepository usersubRepo;
    
    @Autowired
    UserFeedbackRepository userfeedbackRepo;

    @Autowired
    MasUserTokenRepository tokenRepo;
    
    @Autowired
    MasSubscriptionRepository massubRepo;
    
    @Autowired
    TodayQsRepository todayQsRepo;
    
    @Autowired
    UserSubmissionRepository submissionRepo;
    
    @Autowired
    DailyQsRepositry qsRepo;
    
    @Autowired
    LeaderboardRepository leaderoardRepo;

    @Autowired
    TokenService tokenservice;
    
    @Autowired
    InterviewFeedbackRepository interviewFeedbackRepository;

    // -------------------------------------------------------------------
    // LOGIN IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    public ResponseEntity<ApiResponses> loginService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty() || model.getUser_pwd().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            Optional<MasUser> userOpt =
                    userRepo.findByUuidAndUserPwdAndActiveFlag(model.getUuid(), model.getUser_pwd(), "Y");

            if (userOpt.isEmpty()) {
                return response.AppResponse("Notfound", null, null);
            }

            MasUser userDoc = userOpt.get();

            LoginResModel user = new LoginResModel();
            user.setUser_name(userDoc.getUserName());
            user.setUser_mobile(userDoc.getUserMobile());
            user.setUser_email(userDoc.getUserEmail());
            user.setUser_branch(userDoc.getStream());
            user.setUser_inst(userDoc.getUserInst());

            String token = tokenservice.generateToken(model.getUuid(), userDoc.getUserRole());

            // Save token in DB
            MasUserToken tok = new MasUserToken();
            tok.setUuid(model.getUuid());
            tok.setJwt(token);
            tok.setIsInvalid(false);
            tok.setIsLogout(false);
            tok.setEntryTs(Instant.now());
            tokenRepo.save(tok);

            return response.AppResponse("LoginSuccess", token, user);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // -------------------------------------------------------------------
    // REFRESH TOKEN IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    @Transactional
    public ResponseEntity<ApiResponses> refreshTokenService(ResponseBean response, String oldToken) {

        try {
            if (oldToken.isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            if (!tokenservice.validateTokenAndReturnBool(oldToken)) {
                throw new GlobalExceptionHandler.ExpiredException();
            }

            String[] tdata = tokenservice.decodeJWT(oldToken);
            String uuid = tdata[1];

            // Generate new token
            String newToken = tokenservice.generateRefreshToken(oldToken);

            if (newToken == null) {
                return response.AppResponse("TokenValid", null, null);
            }

            // Mark old token as invalid = true
            MasUserToken oldTok = tokenRepo.findByJwt(oldToken).get();
            if (oldTok != null) {
                oldTok.setIsInvalid(true);
                tokenRepo.save(oldTok);
            }

            // Insert new token
            MasUserToken newTok = new MasUserToken();
            newTok.setUuid(uuid);
            newTok.setJwt(newToken);
            newTok.setIsInvalid(false);
            newTok.setIsLogout(false);
            newTok.setEntryTs(Instant.now());
            tokenRepo.save(newTok);

            return response.AppResponse("RefreshSuccess", newToken, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // -------------------------------------------------------------------
    // LOGOUT IMPLEMENTATION (MONGO)
    // -------------------------------------------------------------------
    public ResponseEntity<ApiResponses> logoutService(ResponseBean response, String oldToken) {

        try {
            if (oldToken.isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }

            if (!tokenservice.validateTokenAndReturnBool(oldToken)) {
                throw new GlobalExceptionHandler.ExpiredException();
            }

            MasUserToken tok = tokenRepo.findByJwt(oldToken).get();

            if (tok != null) {
                tok.setIsLogout(true);
                tokenRepo.save(tok);
                return response.AppResponse("LogoutSuccess", null, null);
            }

            return response.AppResponse("Error", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    
    public ResponseEntity<ApiResponses> registerService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty() || model.getUser_pwd().isEmpty() || model.getEmail().isEmpty() || model.getInst().isEmpty()
            		|| model.getBranch().isEmpty() || model.getStream().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }
            
           List<MasUser> userDataCheck=userRepo.findByUserEmailOrUserMobile(model.getEmail(), model.getMobile());
//            System.err.println("--------"+userDataCheck);
            if(!userDataCheck.isEmpty()) {
            	return response.AppResponse("Exists", null, null);
            }
            
            String refCode = new Random().ints(6, 0, 36)
                    .mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i) + "")
                    .collect(Collectors.joining());

            MasUser userData =new MasUser();

            userData.setUserEmail(model.getEmail());
            userData.setUserMobile(model.getMobile());
            userData.setStream(model.getStream());
            userData.setUserBranch(model.getBranch());
            userData.setUserInst(model.getInst());
            userData.setUserName(model.getName());
            userData.setUserPwd(model.getUser_pwd());
            userData.setActiveFlag("Y");
            userData.setUserRole("ADMIN");
            userData.setEntryTs(Instant.now());
            userData.setUuid(model.getUuid());
            userData.setRefCode(refCode);
            
            
            userRepo.save(userData);
            
            UserSubscription userSubscription=new UserSubscription();
            userSubscription.setUuid(model.getUuid());
            userSubscription.setSubType("F");
            userSubscription.setEntryTs(Instant.now());
            
            userSubscription.setRCount(0);
            
            
            Leaderboard lBoardData=new Leaderboard();
            lBoardData.setUuid(model.getUuid());
            lBoardData.setScore(0);
            
            leaderoardRepo.save(lBoardData);
            
            if(model.getRef()!="") {
            Optional<MasUser> refuserData=userRepo.findByRefCode(model.getRef());
            
            if(refuserData.isPresent()) {
            	 userSubscription.setCoin(2);
            	 usersubRepo.save(userSubscription);
            	 UserSubscription refuserSubscription=usersubRepo.findByUuid(refuserData.get().getUuid()).get();
            	 System.out.println(refuserSubscription);
                 if(!"F".equals(refuserSubscription.getSubType()) && refuserSubscription.getRCount()<3){
                 	refuserSubscription.setRCount(refuserSubscription.getRCount()+1);
//                 	refuserSubscription.setTCount(Math.max(0, refuserSubscription.getTCount() - 1));
                 	refuserSubscription.setTCount(refuserSubscription.getTCount() - 1);
                 	refuserSubscription.setCoin(refuserSubscription.getCoin()+2);
                 	
                 	
                 	usersubRepo.save(refuserSubscription);
                 }
            }
           
            }
            else {
            	userSubscription.setCoin(0);
            	 usersubRepo.save(userSubscription);
            }
            return response.AppResponse("RegSuccess", null,null);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    public ResponseEntity<ApiResponses> checkuuidService(ResponseBean response, CommonReqModel model) {

        try {
            if (model.getUuid().isEmpty()) {
                return response.AppResponse("Nulltype", null, null);
            }
            
            
            Optional<MasUser> userData=userRepo.findByUuidAndActiveFlag(model.getUuid(), "Y");

           if(userData.isPresent()) {
        	   return response.AppResponse("Found", null,null);
           }
           else {
        	   return response.AppResponse("SuccessF", null,null);
           }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
 public ResponseEntity<ApiResponses> subscribeService(ResponseBean response, CommonReqModel model, String authToken) {

    try {
        if (authToken == null || authToken.isBlank()) {
            return response.AppResponse("Nulltype", null, null);
        }

        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
            throw new GlobalExceptionHandler.ExpiredException();
        }

        String uuid = tokenservice.decodeJWT(authToken)[1];

        UserSubscription userSub = usersubRepo.findByUuid(uuid).get();
        MasSubscription currentSub = massubRepo.findBySubType(userSub.getSubType()).get();

        String newSubType = model.getType();
        MasSubscription requestedSub = massubRepo.findBySubType(newSubType).get();

        int userUsedCount = userSub.getTCount();
        int currentLimit = currentSub.getLimit();
        int newLimit = requestedSub.getLimit();

        boolean isExhausted = userUsedCount >= currentLimit;

        // ---------------- RULE 1: If user tries to downgrade ----------------
        if (!isUpgradeAllowed(userSub.getSubType(), newSubType)) {

            // downgrade allowed ONLY IF exhausted
            if (!isExhausted) {
                return response.AppResponse("DownGradeNotAllowed",
                        null,
                        currentSub.getSubName());
            }
        }

        // ---------------- RULE 2: Same plan purchase ----------------
        if (userSub.getSubType().equalsIgnoreCase(newSubType)) {

            // if still has remaining count → block
            if (!isExhausted) {
                return response.AppResponse("SubExists",
                        null,
                        currentSub.getSubName());
            }

            // exhausted → allow repurchase (reset count)
            userSub.setTCount(0);
            usersubRepo.save(userSub);

            return response.AppResponse("ReSubscribed",
                    null,
                    currentSub.getSubName());
        }

        // ---------------- RULE 3: Upgrade OR Exhausted Downgrade ----------------
        userSub.setSubType(newSubType);
        userSub.setTCount(0);
        usersubRepo.save(userSub);

        return response.AppResponse("ReSubscribed",
                null,
                requestedSub.getSubName());

    } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
    }
}

 
 
 public ResponseEntity<ApiResponses> saveFeedbackService(ResponseBean response, CommonReqModel model, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String uuid = tokenservice.decodeJWT(authToken)[1];

	        
	        UserFeedback userFeedback=new UserFeedback();
	        
	        userFeedback.setUuid(uuid);
	        userFeedback.setRating(model.getRating());
	        userFeedback.setFeedback(model.getFeedback() != null ? model.getFeedback() : "");

	        userfeedbackRepo.save(userFeedback);
	        
	        return response.AppResponse("fSuccess",
	                null,
	                null);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	}

 public ResponseEntity<ApiResponses> saveDailQSService(ResponseBean response, CommonReqModel model, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        System.err.println(model.getAnswer());
	        
	        String uuid = tokenservice.decodeJWT(authToken)[1];

	        
	        UserSubmission userFeedback=new UserSubmission();
	        
	        userFeedback.setUuid(uuid);
	        userFeedback.setQsId(model.getQsId());
	        userFeedback.setDate(Instant.now());

	        submissionRepo.save(userFeedback);

	        Optional<DailyQs> qdata = qsRepo.findByQsId(model.getQsId());
	        
	        if(qdata.get().getAnswer().equals(model.getAnswer())) {
	        	  
	            Leaderboard lBoardData=leaderoardRepo.findByUuid(uuid).get();
	            lBoardData.setScore(lBoardData.getScore()+5);
	            
	            leaderoardRepo.save(lBoardData);
	        	
	        }
	        
	        return response.AppResponse("fSuccess",
	                null,
	                null);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	} 
 
 
 public ResponseEntity<ApiResponses> leaserboardService(ResponseBean response, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String[] tdata = tokenservice.decodeJWT(authToken);
	        String uuid = tdata[1];

	        // 1) Fetch top 10
	        Pageable top10 = PageRequest.of(0, 10);
	        List<Leaderboard> result = leaderoardRepo.findAllByOrderByScoreDesc(top10);

	        // UUID list of top 10
	        List<String> uuidList = result.stream()
	                .map(Leaderboard::getUuid)
	                .collect(Collectors.toList());

	        // 2) Prepare final response list
	        List<LeaderboardDTO> resData = new ArrayList<>();

	        // 3) Add top 10 users
	        int rank = 1;
	        for (Leaderboard row : result) {

	            Optional<MasUser> userOpt =
	                    userRepo.findByUuidAndActiveFlag(row.getUuid(), "Y");

	            if (userOpt.isEmpty()) continue;

	            LeaderboardDTO dto = new LeaderboardDTO();
	            dto.setName(userOpt.get().getUserName());
	            dto.setScore(row.getScore());
	            dto.setRank(rank);

	            resData.add(dto);
	            rank++;
	        }

	        // 4) Add current user if not in top 10
	        if (!uuidList.contains(uuid)) {

	            // Fetch current user score
	            Leaderboard currentUser = leaderoardRepo.findByUuid(uuid).get();

	            // Fetch user details
	            Optional<MasUser> userOpt =
	                    userRepo.findByUuidAndActiveFlag(uuid, "Y");

	            // CALCULATE GLOBAL RANK
	            List<Leaderboard> all = leaderoardRepo.findAllByOrderByScoreDesc(PageRequest.of(0, 1000));
	            int userRank = IntStream.range(0, all.size())
	                    .filter(i -> all.get(i).getUuid().equals(uuid))
	                    .findFirst()
	                    .orElse(-1) + 1;

	            LeaderboardDTO dto = new LeaderboardDTO();
	            dto.setName(userOpt.get().getUserName());
	            dto.setScore(currentUser.getScore());
	            dto.setRank(userRank);

	            resData.add(dto);
	        }

	        return response.AppResponse("Success", null, resData);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	}

 
 public ResponseEntity<ApiResponses> checksubscribeService(ResponseBean response, CommonReqModel model, String authToken) {

	    try {
	        if (authToken == null || authToken.isBlank()) {
	            return response.AppResponse("Nulltype", null, null);
	        }

	        if (!tokenservice.validateTokenAndReturnBool(authToken)) {
	            throw new GlobalExceptionHandler.ExpiredException();
	        }

	        String uuid = tokenservice.decodeJWT(authToken)[1];

	        UserSubscription userSub = usersubRepo.findByUuid(uuid).get();
	        MasSubscription currentSub = massubRepo.findBySubType(userSub.getSubType()).get();

	        String newSubType = model.getType();
	        MasSubscription requestedSub = massubRepo.findBySubType(newSubType).get();

	        int userUsedCount = userSub.getTCount();
	        int currentLimit = currentSub.getLimit();
	        int newLimit = requestedSub.getLimit();

	        boolean isExhausted = userUsedCount >= currentLimit;

	        // ---------------- RULE 1: If user tries to downgrade ----------------
	        if (!isUpgradeAllowed(userSub.getSubType(), newSubType)) {

	            // downgrade allowed ONLY IF exhausted
	            if (!isExhausted) {
	                return response.AppResponse("DownGradeNotAllowed",
	                        null,
	                        currentSub.getSubName());
	            }
	        }

	        // ---------------- RULE 2: Same plan purchase ----------------
	        if (userSub.getSubType().equalsIgnoreCase(newSubType)) {

	            // if still has remaining count → block
	            if (!isExhausted) {
	                return response.AppResponse("SubExists",
	                        null,
	                        currentSub.getSubName());
	            }

	            

	            return response.AppResponse("ReSubscribed",
	                    null,
	                    currentSub.getSubName());
	        }

	      

	        return response.AppResponse("ReSubscribed",
	                null,
	                requestedSub.getSubName());

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        throw ex;
	    }
	}

 
   private boolean isUpgradeAllowed(String current, String next) {

	    // Ordering: Basic < Silver < Gold
	    int rankCurrent = getRank(current);
	    int rankNext = getRank(next);
	    System.out.println(current+"--"+next);
	    System.out.println(rankCurrent+"--"+rankNext);
	    return rankNext >= rankCurrent;  // allow upgrade or same plan
	}

	private int getRank(String sub) {
	    return switch (sub.toLowerCase()) {
	        case "b" -> 1;
	        case "s" -> 2;
	        case "g" -> 3;
	        default -> 0;
	    };
	}

    
    
    public ResponseEntity<ApiResponses> profileService(ResponseBean response, String authToken) {

        try {
        	if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
            
            String[] tdata = tokenservice.decodeJWT(authToken);
            String uuid = tdata[1];
            MasUser userData=userRepo.findByUuidAndActiveFlag(uuid, "Y").get();
            UserSubscription usesubData =usersubRepo.findByUuid(uuid).get();
            MasSubscription masSub=massubRepo.findBySubType(usesubData.getSubType()).get();
            
            
            ProfileDTO profile=new ProfileDTO();
            
            profile.setName(userData.getUserName());
            profile.setMobile(userData.getUserMobile());
            profile.setEmail(userData.getUserEmail());
            profile.setInstitute(userData.getUserInst());
            profile.setStream(userData.getStream());
            profile.setCity(userData.getUserBranch());
            profile.setSubName(masSub.getSubName());
            profile.setIntCount(usesubData.getCount());
            profile.setRefCount(usesubData.getRCount());
            profile.setRef(userData.getRefCode());
            profile.setTCount(masSub.getLimit()-usesubData.getTCount());
            profile.setCreationData(userData.getEntryTs());
            profile.setCoin(usesubData.getCoin());
          
            return response.AppResponse("Success", null,profile);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    public ResponseEntity<ApiResponses> viewRecordsService(ResponseBean response, String authToken) {

        try {
        	if(authToken.isBlank() || authToken.isEmpty()) {
				return response.AppResponse("Nulltype", null, null);
			}
			
			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
				throw new GlobalExceptionHandler.ExpiredException();
			}
            
            String[] tdata = tokenservice.decodeJWT(authToken);
            String uuid = tdata[1];
            
            
            
            List<InterviewFeedback> feedback=interviewFeedbackRepository.findByUuid(uuid);
          if(!feedback.isEmpty()) {
        	  return response.AppResponse("Success", null,feedback);
          }
          else {
        	  return response.AppResponse("Notfound", null,null);
          }
            
           

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public ResponseEntity<ApiResponses> viewDailyQSService(ResponseBean response, String authToken) {

        try {

            if (authToken == null || authToken.isBlank()) {
                return response.AppResponse("Nulltype", null, null);
            }

            if (!tokenservice.validateTokenAndReturnBool(authToken)) {
                throw new GlobalExceptionHandler.ExpiredException();
            }

            String[] tdata = tokenservice.decodeJWT(authToken);
            String uuid = tdata[1];

            Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant todayEnd = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

            List<DailyQsDTO> dailyQs = new ArrayList<>();

            List<String> tech = List.of("Java", "SQL", "Python", "JavaScript");

            // ================================
            // 1️⃣ CHECK IF TODAY’s QS ALREADY EXISTS
            // ================================
            List<TodayQs> todayQsList = todayQsRepo.findByDateBetween(todayStart, todayEnd);

            List<Long> qsIdsForToday = new ArrayList<>();
            if (!todayQsList.isEmpty()) {
                qsIdsForToday = todayQsList.stream()
                        .map(TodayQs::getQsId)
                        .collect(Collectors.toList());
            }

            // ================================
            // 2️⃣ IF NOT EXISTS → GENERATE RANDOM 4 QUESTIONS & SAVE
            // ================================
            if (qsIdsForToday.isEmpty()) {

                for (String lang : tech) {
                    DailyQs qs = qsRepo.findRandomOneByLang(lang).get(0);

                    TodayQs todayQs = new TodayQs();
                    todayQs.setQsId(qs.getQsId());
                    todayQs.setDate(Instant.now());

                    todayQsRepo.save(todayQs);

                    qsIdsForToday.add(qs.getQsId());
                }
            }
//            System.out.println(qsIdsForToday);
            // ================================
            // 3️⃣ FETCH USER SUBMISSION FOR TODAY
            // ================================
            List<UserSubmission> submissionDataToday =
                    submissionRepo.findByUuidAndDateBetween(uuid, todayStart, todayEnd);
//            System.out.println(submissionDataToday);
            
            Set<Long> submittedTodayIds = submissionDataToday.stream()
                    .map(UserSubmission::getQsId)
                    .collect(Collectors.toSet());
//            System.err.println(submittedTodayIds);
            // ================================
            // 4️⃣ PREPARE RESPONSE USING THE TODAY’s QS
            // ================================
            for (Long qsId : qsIdsForToday) {
                Optional<DailyQs> qdata = qsRepo.findByQsId(qsId);
//                System.out.println(qdata);
                if (qdata.isEmpty()) continue;

                DailyQs qs = qdata.get();
                DailyQsDTO dto = new DailyQsDTO();

                dto.setId(qs.getQsId());
                dto.setLang(qs.getLang());
                dto.setQuestion(qs.getQuestion());
                dto.setOptions(new String[]{
                    qs.getOption1(),
                    qs.getOption2(),
                    qs.getOption3(),
                    qs.getOption4()
                });

                // Mark submit Y/N FOR TODAY
                if (submittedTodayIds.contains(qs.getQsId())) {
                    dto.setSubmit("Y");
                } else {
                    dto.setSubmit("N");
                }

                dailyQs.add(dto);
            }

            if (!dailyQs.isEmpty()) {
                return response.AppResponse("Success", null, dailyQs);
            } else {
                return response.AppResponse("Notfound", null, null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    
//    public ResponseEntity<ApiResponses> viewDailyQSService(ResponseBean response, String authToken) {
//
//        try {
//        	if(authToken.isBlank() || authToken.isEmpty()) {
//				return response.AppResponse("Nulltype", null, null);
//			}
//			
//			if(!tokenservice.validateTokenAndReturnBool(authToken)) {
//				throw new GlobalExceptionHandler.ExpiredException();
//			}
//            
//			String[] tdata = tokenservice.decodeJWT(authToken);
//            String uuid = tdata[1];
//            
//            List<DailyQsDTO> dailyQs=new ArrayList<>();
//            
//            List<String> tech = List.of("Java", "SQL", "Python", "JavaScript");
//
//            List<UserSubmission> submissionData = submissionRepo.findByUuid(uuid);
//            Set<Long> submittedQuestionIds = submissionData.stream()
//                    .map(sub -> sub.getQsId())
//                    .collect(Collectors.toSet());
//
//            for (String lang : tech) {
//                DailyQs qs = qsRepo.findRandomOneByLang(lang).get(0);
//                DailyQsDTO qsDTO = new DailyQsDTO();
//                qsDTO.setId(Long.parseLong(qs.getQsId()));
//                qsDTO.setLang(qs.getLang());
//                qsDTO.setQuestion(qs.getQuestion());
//
//                // Set options
//                String[] options = new String[] {
//                    qs.getOption1(),
//                    qs.getOption2(),
//                    qs.getOption3(),
//                    qs.getOption4()
//                };
//                qsDTO.setOptions(options);
//                
//                
//                
//                // Mark submit as "Y" if user has already submitted
//                if (submittedQuestionIds.contains(Long.parseLong(qs.getQsId()))) {
//                    qsDTO.setSubmit("Y");
//                } else {
//                    qsDTO.setSubmit("N");
//                }
//
//                dailyQs.add(qsDTO);
//            }
//
//            if (!dailyQs.isEmpty()) {
//                return response.AppResponse("Success", null, dailyQs);
//            }
//
//          else {
//        	  return response.AppResponse("Notfound", null,null);
//          }
//            
//           
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        }
//    }
}
