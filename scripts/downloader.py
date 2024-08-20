import csv
import os
import requests
import time
from urllib.parse import urlsplit

# Configuración
archivo_csv = 'RESULTADOS_2024_CSV_V2.csv'  # Nombre del archivo CSV
columna_url = 'URL'  # Nombre de la columna con las URLs
carpeta_destino = '.'  # Carpeta donde se guardarán las imágenes
descargas_concurrentes = 10  # Número de descargas concurrentes
tiempo_espera = 0.5  # Tiempo de espera entre descargas (en segundos)

# Verificar si el archivo CSV existe
if not os.path.isfile(archivo_csv):
    print(f"El archivo '{archivo_csv}' no existe.")
    exit()

# Función para descargar una imagen
def descargar_imagen(url, nombre_archivo):
    # Verificar si la imagen ya existe
    if os.path.isfile(nombre_archivo):
        print(f"La imagen '{nombre_archivo}' ya existe. Saltando...")
        return

    respuesta = requests.get(url)
    if respuesta.status_code == 200:
        with open(nombre_archivo, 'wb') as archivo:
            archivo.write(respuesta.content)
        print(f'Imagen descargada: {nombre_archivo}')
    else:
        print(f'Error al descargar {url}')

# Leer el archivo CSV y descargar las imágenes
with open(archivo_csv, 'r', -1, 'ISO-8859-1') as archivo:
    lector_csv = csv.DictReader(archivo)
    for fila in lector_csv:
        url = fila[columna_url]
        nombre_archivo = os.path.join(carpeta_destino, os.path.basename(urlsplit(url).path))
        descargar_imagen(url, nombre_archivo)

        # Control de velocidad
        if lector_csv.line_num % descargas_concurrentes == 0:
            print(f'Esperando {tiempo_espera} segundos antes de continuar...')
            time.sleep(tiempo_espera)


