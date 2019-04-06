# SpeechSmartHome
speech recognition and segmentation  for smart_home project
#基本流程
mainActivity 点击话筒-->SpeakActivity语音功能界面-->点击开始录音，即开始识别，分词
-->分词结果以alertDialog显示，可编辑-->点击确认，即生成指令entity，点击取消，即不生成指令


#缺陷
1.dialog部分比较简陋
2.未添加设备过滤，可能对不存在的设备发出指令
3.返回键的id没找到，直接打印出来，找到使用的int值，不清楚，不过问题不大

