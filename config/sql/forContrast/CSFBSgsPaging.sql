# CSFB SGs寻呼响应次数
SELECT fileid,COUNT(*),sum(case when ProcedureStatus=0 then 1 else 0 end) from sgs WHERE ProcedureType=1 AND ServiceIndicator<>2 group by fileid