from flask import Flask, jsonify, request, render_template
import cv2
import json
import base64
import time
import pickle
import requests
import numpy as np
from PIL import Image
from io import BytesIO


def post_frame():
    img = Image.open("7.png ")

    #Convert Pillow Image to bytes and then to base64
    buffered = BytesIO()
    img.save(buffered, format="png")
    img_byte = buffered.getvalue() # bytes
    img_base64 = base64.b64encode(img_byte) #Base64-encoded bytes * not str

    #It's still bytes so json.Convert to str to dumps(Because the json element does not support bytes type)
    img_str = img_base64.decode('utf-8') # str

    files = {
        "text":"geo",
        "img":img_str
        }

    r = requests.post("http://127.0.0.1:5000", json=json.dumps(files)) #POST to server as json

    print(r.json())


face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')


cap = cv2.VideoCapture(0)

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()
    gray  = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.5, minNeighbors=5)
    cv2.imwrite('7'+'.png', frame)
    post_frame()
    for (x, y, w, h) in faces:

        roi_gray = gray[y:y+h, x:x+w]
        roi_color = frame[y:y+h, x:x+w]
        
        color = (255, 0, 0) 
        stroke = 2
        end_cord_x = x + w
        end_cord_y = y + h
        cv2.rectangle(frame, (x, y), (end_cord_x, end_cord_y), color, stroke)

    cv2.imshow('frame',frame)
    if cv2.waitKey(20) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()


