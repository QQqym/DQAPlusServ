#无线掉线率
# 正常的eNB请求释放上下文数
SELECT COUNT(*) from s1_mme where RequestCause IN(20,23,24,28,36) AND ProcedureType=20 and Keyword_1=1