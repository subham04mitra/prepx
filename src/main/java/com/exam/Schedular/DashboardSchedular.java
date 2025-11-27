package com.exam.Schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DashboardSchedular {

	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
	 
	 private static final String TRUNCATE_SQL_teacher = "TRUNCATE TABLE masadmin.teacher_history";

	    private static final String INSERT_SQL_teacher = """
	        INSERT INTO masadmin.teacher_history("uuid", tot_paper, tot_scan)
	        SELECT mu."uuid", COUNT(x.paper_id), 0
	        FROM emsadmin.paper_details x
	        JOIN masadmin.mas_user mu ON x.created_by = mu."uuid"
	        WHERE mu.user_role = 'TEACHER'
	        GROUP BY mu."uuid";
	    """;
	    private static final String TRUNCATE_SQL_dashboard = "truncate masadmin.dashboard_data";

	    private static final String INSERT_SQL_dashboard = """
	       INSERT INTO masadmin.dashboard_data
(inst, branch, teacher, paper_generated, omr_scan, avg_marks)
select
    COALESCE(p.user_inst, t.user_inst) AS user_inst,
    COALESCE(p.user_branch, t.user_branch) AS user_branch,
    COALESCE(t.teacher_count, 0) AS teacher_count,
    COALESCE(p.paper_count, 0) AS paper_count,
    0,0
FROM
    (
        SELECT mu.user_branch, mu.user_inst, COUNT(x.paper_id) AS paper_count
        FROM emsadmin.paper_details x
        JOIN masadmin.mas_user mu ON x.created_by = mu."uuid"
        GROUP BY mu.user_branch, mu.user_inst
    ) p
FULL OUTER JOIN
    (
        SELECT mu.user_branch, mu.user_inst, COUNT(*) AS teacher_count
        FROM masadmin.mas_user mu
        WHERE user_role = 'TEACHER'
        GROUP BY mu.user_branch, mu.user_inst
    ) t
ON p.user_branch = t.user_branch AND p.user_inst = t.user_inst
ORDER BY user_branch, user_inst;

	    """;
//	    @Scheduled(cron = "0 0 * * * *")
	    @Scheduled(cron = "0 0 6 * * *") 
	    public void refreshTeacherHistory() {
	        jdbcTemplate.execute(TRUNCATE_SQL_teacher);
	        jdbcTemplate.execute(INSERT_SQL_teacher);
	        jdbcTemplate.execute(TRUNCATE_SQL_dashboard);
	        jdbcTemplate.execute(INSERT_SQL_dashboard);

	        System.out.println("✅ stats refreshed at " + java.time.LocalDateTime.now());
	    }
}
