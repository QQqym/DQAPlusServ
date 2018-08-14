# 关键字段统计
# IMEI完整数
SELECT count(*) from s1_mme where IMEI<>0 AND IMEI IS NOT NULL