# 关键字段统计
# MSISDN 完整数
SELECT count(*) from s1_mme where MSISDN<>0 and MSISDN IS NOT NULL