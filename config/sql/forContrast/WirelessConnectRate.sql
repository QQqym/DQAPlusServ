#无线接通率按照 CellID 分组统计
SELECT mme.CellID, mme.`all` as "ERAB.NbrAttEstab", mme.success as "ERAB.NbrSuccEstab"
FROM 
(SELECT CellID,
	SUM(
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
	) as 'success',
	SUM(
		(CASE WHEN Bearer1Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer2Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer3Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer4Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer5Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer6Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer7Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer8Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer9Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer10Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer11Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer12Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer13Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer14Status in (1,2) THEN 1 ELSE 0 END)
	+(CASE WHEN Bearer15Status in (1,2) THEN 1 ELSE 0 END)
	) as 'all'
 FROM s1_mme WHERE ProcedureType IN (2,3,5,7,9,10,13)
 GROUP BY CellID
) as mme
