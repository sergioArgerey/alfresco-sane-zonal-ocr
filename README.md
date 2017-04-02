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
<!---
[![	Actions)](https://cloud.githubusercontent.com/assets/24793099/24570188/81f36462-166a-11e7-94c1-da91a78c8f1b.png)]()
-->
## Contextual actions
[![	Actions)](https://cloud.githubusercontent.com/assets/24793099/24570198/8c95e20a-166a-11e7-8d2f-569926fdddd9.png)]()


## Zonal OCR Profiler included in SHARE
Temporary access:
http://localhost:8081/share/page/hdp/ws/zonal-ocr-profiler



![Zonal OCR Profiler](https://cloud.githubusercontent.com/assets/24793099/24427095/0e5b2d8a-140a-11e7-9b34-6a1fc5dd5df9.png)


## Todo list
- [x] Split into different actions (Scan, Scan with OCR, OCR)
- [x] Change the access of profiler to admin tools
- [ ] Clean & refactor old java code
- [ ] Use tesseract securely (JNA, JNI)
