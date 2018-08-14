#SGW从MME接收专用承载建立成功个数
SELECT fileid,COUNT(*) ,sum(case when ProcedureStatus=0 then 1 else 0 end) from s10 WHERE ProcedureType=1 group by fileid