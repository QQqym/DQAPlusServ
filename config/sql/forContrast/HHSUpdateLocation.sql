#更新位置成功次数
SELECT fileid ,COUNT(*) ,sum(case when ProcedureStatus=0 then 1 else 0 end)  from s6a WHERE  ProcedureType=1 group by fileid
