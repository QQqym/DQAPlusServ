# 无线接通率 
# E-RAB建立成功数
 SELECT SUM(
		(CASE WHEN Bearer1Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer2Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer3Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer4Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer5Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer6Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer7Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer8Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer9Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer10Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer11Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer12Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer13Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer14Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer15Status=1 THEN 1 ELSE 0 END)
		+(CASE WHEN Bearer16Status=1 THEN 1 ELSE 0 END)
		)
	 from s1_mme WHERE ProcedureType IN (2,7,9,10,13,3,5)