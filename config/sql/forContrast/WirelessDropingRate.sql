#无线掉线率按照CellID分组统计
SELECT c.CellID as 'CellID'
,c.`CONTEXT.AttRelEnb` as "CONTEXT.AttRelEnb"
,c.`CONTEXT.AttRelEnb.Normal` as "CONTEXT.AttRelEnb.Normal"
,c.`CONTEXT.SuccInitalSetup` as "CONTEXT.SuccInitalSetup"
,c.NbrLeft+u.NbrLeft as 'CONTEXT.NbrLeft'
FROM
(	SELECT CellID ,
		SUM(CASE WHEN ProcedureType=20 AND Keyword_1=0 THEN 1 ELSE 0 END) AS 'CONTEXT.AttRelEnb',
		SUM(CASE WHEN RequestCause IN(20,23,24,28,36) AND ProcedureType=20 AND Keyword_1=0 THEN 1 ELSE 0 END) AS 'CONTEXT.AttRelEnb.Normal',
		SUM(CASE WHEN ProcedureType=18 AND ProcedureStatus=0 THEN 1 ELSE 0 END) AS 'CONTEXT.SuccInitalSetup',
		SUM(CASE WHEN ProcedureStatus=0 AND ProcedureType IN (18,15) THEN 1 ELSE 0 END)
		- SUM(CASE WHEN ProcedureStatus=0 AND ProcedureType IN (20,16) THEN 1 ELSE 0 END) AS 'NbrLeft'
	FROM s1_mme
	GROUP BY CellID
) AS c LEFT JOIN
( SELECT CellID ,
		SUM(CASE WHEN L4Protocol=0 and appType=15 THEN 1 ELSE 0 END)
		- SUM(CASE WHEN L4Protocol=0 and appType=16 THEN 1 ELSE 0 END) AS 'NbrLeft'
	FROM s1_u
	GROUP BY CellID
) AS u 
ON c.CellID=u.CellID