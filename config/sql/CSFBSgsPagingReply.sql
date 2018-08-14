# CSFB SGs寻呼响应次数
SELECT COUNT(*) from sgs WHERE ProcedureType=1 AND ProcedureStatus=0 AND ServiceIndicator<>2