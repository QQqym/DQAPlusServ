# 关键字段统计
# s1_mme 接口XDR 关键字段完整率
SELECT 
	count(*) as 'Total',
	SUM(case  when IMEI IS NOT NULL AND IMEI<>'' then 1 ELSE 0 END) AS 'IMEI',
	SUM(case  when IMSI IS NOT NULL AND IMSI<>'' then 1 ELSE 0 END) AS 'IMSI',
	SUM(case  when MSISDN IS NOT NULL AND MSISDN<>'' then 1 ELSE 0 END) AS 'MSISDN',
	SUM(case  when CellID IS NOT NULL AND CellID<>'' then 1 ELSE 0 END) AS 'CellID',
	SUM(case  when MmeUeS1ApId IS NOT NULL AND MmeUeS1ApId<>'' then 1 ELSE 0 END) AS 'MmeUeS1ApId'
from s1_mme where 0=0