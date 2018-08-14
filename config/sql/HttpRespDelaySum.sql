# 页面响应时长
SELECT SUM(HttpFirstRespondDelay) from http WHERE HttpFirstRespondDelay >0