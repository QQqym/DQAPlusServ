# 关键字段统计
# IMSI完整数
SELECT count(*) from s1_mme where IMSI<>0 AND IMSI is NOT NULL