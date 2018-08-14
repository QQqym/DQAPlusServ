#2、下载速率之和  单请求
SELECT   sum(8*DlDataLen/(1000*HttpLastPacketDelay))  from http
