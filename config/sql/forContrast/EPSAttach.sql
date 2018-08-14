#EPS附着请求成功次数
SELECT MMECode ,COUNT(*),sum(case when procedureStatus=0 then 1 else 0 end) from s1_mme WHERE ProcedureType=1 group by MMECode
