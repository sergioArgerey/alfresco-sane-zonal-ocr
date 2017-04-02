# alfresco-sane-zonal-ocr
Zonal OCR for scanners based on SANE integrated with Alfresco/Share

<!---
[![	temporary status (simple build status)](https://img.shields.io/teamcity/http/teamcity.jetbrains.com/s/bt345.svg)]()
-->


```bash
~/alfresco-sane-zonal-ocr$ cd sane-zonal-ocr-module-alfresco/
~/alfresco-sane-zonal-ocr/sane-zonal-ocr-module-alfresco$ ./run.sh 
```

```bash
~/alfresco-sane-zonal-ocr$ cd sane-zonal-ocr-module-share/
~/alfresco-sane-zonal-ocr/sane-zonal-ocr-module-share$ ./run.sh 
```

## Command line network sane client
![command-line-sane-network-client](https://cloud.githubusercontent.com/assets/24793099/24588722/71662ac0-17ce-11e7-92da-3f00ab99f8fa.png)


<!---
![	Actions)](https://cloud.githubusercontent.com/assets/24793099/24570188/81f36462-166a-11e7-94c1-da91a78c8f1b.png)
-->
## Contextual actions
![Actions)](https://cloud.githubusercontent.com/assets/24793099/24570198/8c95e20a-166a-11e7-8d2f-569926fdddd9.png)


## Zonal OCR Profiler included in SHARE
![admin-tools-menu](https://cloud.githubusercontent.com/assets/24793099/24587092/2bec52e4-17b0-11e7-82b3-4c49724f70bd.png)
Or direct link:
http://localhost:8081/share/page/hdp/ws/zonal-ocr-profiler




![Zonal OCR Profiler](https://cloud.githubusercontent.com/assets/24793099/24427095/0e5b2d8a-140a-11e7-9b34-6a1fc5dd5df9.png)


## Todo list
- [x] Split into different actions (Scan, Scan with OCR, OCR)
- [x] Change the access of profiler to admin tools
- [ ] Add Network Sane Profiler JSON test with JSON schema
- [ ] i18n references on admin tools menu
- [ ] Clean & refactor old java code
- [ ] Use tesseract securely (JNA, JNI)
