#无线侧OMC和DQA指标比对SQL语句,结尾不可有;分号

SELECT omc.startTime,omc.timeGranularity,omc.CellID
,omc.ERAB_NbrAttEstab as 'omc.ERAB_NbrAttEstab'
,dqa.ERAB_NbrAttEstab as 'dqa.ERAB_NbrAttEstab'
,(omc.ERAB_NbrAttEstab-dqa.ERAB_NbrAttEstab)/omc.ERAB_NbrAttEstab as 'rateErab'
,omc.ERAB_NbrSuccEstab as 'omc.ERAB_NbrSuccEstab'
,dqa.ERAB_NbrSuccEstab as 'dqa.ERAB_NbrSuccEstab'
,(omc.ERAB_NbrSuccEstab-dqa.ERAB_NbrSuccEstab)/omc.ERAB_NbrSuccEstab as 'rateErabSuc'
,omc.RRC_AttConnEstab as 'omc.RRC_AttConnEstab'
,''  as 'omc.RRC_AttConnEstab'
,'' as 'raterrc'
,omc.RRC_SuccConnEstab
,'' as  'dqa.RRC_SuccConnEstab'
,'' as 'rateErabSuc'
,omc.CONTEXT_AttRelEnb as 'omc.CONTEXT_AttRelEnb'
,dqa.CONTEXT_AttRelEnb as 'dqa.CONTEXT_AttRelEnb'
,(omc.CONTEXT_AttRelEnb-dqa.CONTEXT_AttRelEnb)/omc.CONTEXT_AttRelEnb as 'CONTEXT_AttRelEnb'
,omc.CONTEXT_AttRelEnb_Normal as 'omc.CONTEXT_AttRelEnb_Normal'
,dqa.CONTEXT_AttRelEnb_Normal as 'dqa.CONTEXT_AttRelEnb_Normal'
,(omc.CONTEXT_AttRelEnb_Normal-dqa.CONTEXT_AttRelEnb_Normal)/omc.CONTEXT_AttRelEnb_Normal as 'CONTEXT_AttRelEnb_Normal'
,omc.CONTEXT_SuccInitalSetup as 'omc.CONTEXT_SuccInitalSetup'
,dqa.CONTEXT_SuccInitalSetup as 'dqa.CONTEXT_SuccInitalSetup'
,(omc.CONTEXT_SuccInitalSetup-dqa.CONTEXT_SuccInitalSetup)/omc.CONTEXT_SuccInitalSetup as 'rateCONTEXT_SuccInitalSetup'
,omc.CONTEXT_NbrLeft as 'omc.CONTEXT_NbrLeft'
,dqa.CONTEXT_NbrLeft as 'dqa.CONTEXT_NbrLeft'
,(omc.CONTEXT_NbrLeft-dqa.CONTEXT_NbrLeft)/omc.CONTEXT_NbrLeft as 'rateCONTEXT_NbrLeft'
FROM wirelessside_omc omc
LEFT JOIN wirelessside_dqa dqa
ON omc.CellID = dqa.CellID