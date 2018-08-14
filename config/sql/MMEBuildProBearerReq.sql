# MME请求建立的专用S5S8承载数
SELECT count(*) from S10 where ProcedureType=1 
	AND (Bearer1Type=2 or Bearer2Type=2 or Bearer3Type=2 or Bearer4Type=2 
		 or Bearer5Type=2  or Bearer6Type=2  or Bearer7Type=2  or Bearer8Type=2 
		 or Bearer9Type=2 or Bearer10Type=2 or Bearer11Type=2 or Bearer12Type=2
		 or Bearer13Type=2 or Bearer14Type=2  or Bearer15Type=2 or Bearer16Type=2)