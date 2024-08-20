# records-qr-scanner

This tool was built to try out QR code scanning libraries in Java, and parallelism.

I've used data from Venezuela's 2024 Presidential Elections because I was interested in the results.

## DISCLAIMER

The tool does NOT validate the veracity of the data, as it's not part of its responsibilities,
it simply counts the results of the images it is able to understand.

## Partial Results

This is the result of processing the images linked in the following CSV
file: [RESULTADOS_2024_CSV_V2.csv](https://static.resultadosconvzla.com/RESULTADOS_2024_CSV_V2.csv). [^1]

```
Files to process: 25075
Scan - Elapsed time: 1347749 ms
Count - Elapsed time: 231 ms
------------------------------------------------------------------------------------
Files processed:   25066
 - Without errors: 23223 (92.65 %)
 - With errors:     1843 (7.35 %)
------------------------------------------------------------------------------------
 -       NICOLAS MADURO:      3072403 ( 30.60 %)
 -        LUIS MARTINEZ:        79390 (  0.79 %)
 -      JAVIER BERTUCCI:        18755 (  0.19 %)
 -           JOSE BRITO:        20346 (  0.20 %)
 -       ANTONIO ECARRI:        47124 (  0.47 %)
 -       CLAUDIO FERMIN:        11539 (  0.11 %)
 -      DANIEL CEBALLOS:         9757 (  0.10 %)
 -     EDMUNDO GONZALEZ:      6720398 ( 66.94 %)
 -      ENRIQUE MARQUEZ:        23955 (  0.24 %)
 -     BENJAMIN RAUSSEO:        35510 (  0.35 %)
------------------------------------------------------------------------------------
 -          Valid votes:     10039177
 -           Null votes:         1119
 -          Empty votes:            0

```

It took ~22 minutes to process 25.066 QR codes in my old laptop (Intel® Core™ i5-3210M CPU @ 2.50GHz × 4),
using the BoofCV[^2] reader, which was able to process successfully 92.65% of the images.

## Other content in the repository

### scripts

#### downloader.py

Inside the `script` folder you'll find the `downloader.py` script which I used to download the images using the CSV file
to get the URls.

---

[^1]: Downloaded from https://resultadosconvzla.com/
[^2]: https://boofcv.org/
