# 用户侧指标
SELECT
	d.UlDataLen+f.UlDataLen+v.UlDataLen+r.UlDataLen+i.UlDataLen+s.UlDataLen+h.UlDataLen+p.UlDataLen+e.UlDataLen+m.DlDataLen as 'ULTCPDataVolume' -- --1)上行流量
  ,d.DlDataLen+f.DlDataLen+v.DlDataLen+r.DlDataLen+i.DlDataLen+s.DlDataLen+h.DlDataLen+p.DlDataLen+e.DlDataLen+m.DlDataLen as 'DLTCPDataVolume' -- --2)下行流量
  ,d.UlIpPacketNum+f.UlIpPacketNum+v.UlIpPacketNum+r.UlIpPacketNum+i.UlIpPacketNum+s.UlIpPacketNum
		+h.UlIpPacketNum+p.UlIpPacketNum+e.UlIpPacketNum+m.UlIpPacketNum as 'UlIpPacketNum' -- --3)上行TCP发送报文数
  ,d.DlIpPacketNum+f.DlIpPacketNum+v.DlIpPacketNum+r.DlIpPacketNum+i.DlIpPacketNum+s.DlIpPacketNum
		+h.DlIpPacketNum+p.DlIpPacketNum+e.DlIpPacketNum+m.DlIpPacketNum as 'DlIpPacketNum' -- --4)下行TCP发送报文数
  ,d.UlTCPOutOfOrderPacketNum+f.UlTCPOutOfOrderPacketNum+v.UlTCPOutOfOrderPacketNum
		+r.UlTCPOutOfOrderPacketNum+i.UlTCPOutOfOrderPacketNum+s.UlTCPOutOfOrderPacketNum
		+h.UlTCPOutOfOrderPacketNum+p.UlTCPOutOfOrderPacketNum+e.UlTCPOutOfOrderPacketNum
		+m.UlTCPOutOfOrderPacketNum as 'UlTCPMissequencePacketCount' -- --5)上行乱序报文数
  ,d.DlTCPOutOfOrderPacketNum+f.DlTCPOutOfOrderPacketNum+v.DlTCPOutOfOrderPacketNum
		+r.DlTCPOutOfOrderPacketNum+i.DlTCPOutOfOrderPacketNum+s.DlTCPOutOfOrderPacketNum
		+h.DlTCPOutOfOrderPacketNum+p.DlTCPOutOfOrderPacketNum+e.DlTCPOutOfOrderPacketNum
		+m.DlTCPOutOfOrderPacketNum as 'DlTCPMissequencePacketCount'  -- --6)下行乱序报文数
  ,d.UlTCPReTransPacketNum+f.UlTCPReTransPacketNum+v.UlTCPReTransPacketNum
		+r.UlTCPReTransPacketNum+i.UlTCPReTransPacketNum+s.UlTCPReTransPacketNum
		+h.UlTCPReTransPacketNum+p.UlTCPReTransPacketNum+e.UlTCPReTransPacketNum
		+m.UlTCPReTransPacketNum as 'UlTCPReTransmitPacketCount' -- --7)上行重传报文数
  ,d.DlTCPReTransPacketNum+f.DlTCPReTransPacketNum+v.DlTCPReTransPacketNum
		+r.DlTCPReTransPacketNum+i.DlTCPReTransPacketNum+s.DlTCPReTransPacketNum
		+h.DlTCPReTransPacketNum+p.DlTCPReTransPacketNum+e.DlTCPReTransPacketNum
		+m.DlTCPReTransPacketNum as 'UlTCPReTransmitPacketCount' -- --8)下行重传报文数
	,h.httpCount as 'HttpReqCount' -- --9)Http请求数(网页XDR数)
	,h.HttpRespSuccCount as 'HttpResponseSuccCount' -- -- 10)Http响应成功数
	,h.HttpFirstRespondDelay as 'HttpAvgRespDelay' -- --11)Http响应时长
	,h.CompleteCount as 'HttpDisplaySuccCount'  -- --12)Http显示成功数
	,h.HttpLastPacketDelay/h.HttpDisplaySuccCount as 'HttpAvgDisplayDelay'  -- -- 13)Http显示时长
	,h.HttpDLTotalRate as 'HttpTotalDLRate'  -- -- 14)Http下载速率和
	,h.HttpDLTotalRate/h.HttpDisplaySuccCount as 'HttpAvgDLRate'  -- -- 15)Http平均下载速率
FROM
	(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
	from dns WHERE 0=0 ) as d
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from ftp WHERE 0=0 ) as f
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from voip WHERE 0=0) as v
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from rtsp WHERE 0=0 ) as r
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from im WHERE 0=0 ) as i
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from p2p WHERE 0=0 ) as p
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from email WHERE 0=0 ) as e
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from mms WHERE 0=0 ) as m
	,(SELECT COALESCE(SUM(case WHEN L4Protocol=0 then UlDataLen ELSE 0 END),0) AS 'UlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then dlDataLen ELSE 0 END),0) as 'DlDataLen'
			,COALESCE(SUM(case WHEN L4Protocol=0 then UlIpPacketNum ELSE 0 END),0) as 'UlIpPacketNum'
			,COALESCE(SUM(case WHEN L4Protocol=0 then DlIpPacketNum ELSE 0 END),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
		from s1_u WHERE 0=0 ) as s
	,(SELECT COALESCE(SUM(UlDataLen),0) AS 'UlDataLen'
			,COALESCE(SUM(dlDataLen),0) as 'DlDataLen'
			,COALESCE(SUM(UlIpPacketNum),0) as 'UlIpPacketNum'
			,COALESCE(SUM(DlIpPacketNum),0) as 'DlIpPacketNum'
			,COALESCE(SUM(UlTCPOutOfOrderPacketNum),0) as 'UlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(DlTCPOutOfOrderPacketNum),0) as 'DlTCPOutOfOrderPacketNum'
			,COALESCE(SUM(UlTCPReTransPacketNum),0) as 'UlTCPReTransPacketNum'
			,COALESCE(SUM(DlTCPReTransPacketNum),0) as 'DlTCPReTransPacketNum'
			,COUNT(*) as 'httpCount'
			,COALESCE(SUM(case WHEN HttpFirstRespondDelay >0  then 1 ELSE 0 END),0) as 'HttpRespSuccCount'
			,COALESCE(SUM(HttpFirstRespondDelay),0) as 'HttpFirstRespondDelay'
			,COALESCE(SUM(case WHEN CompleteFlag=0  then 1 ELSE 0 END),0) as 'CompleteCount'
			,COALESCE(SUM(case WHEN HttpLastPacketDelay>0  then 1 ELSE 0 END),0) as 'HttpDisplaySuccCount'
			,COALESCE(SUM(HttpLastPacketDelay),0) as 'HttpLastPacketDelay'
			,COALESCE(SUM(DlDataLen*8/HttpLastPacketDelay/1000),0) as 'HttpDLTotalRate'
		from http WHERE 0=0 ) as h
	