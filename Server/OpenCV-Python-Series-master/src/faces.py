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

app = Flask(__name__)

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
eye_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_eye.xml')
smile_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_smile.xml')


recognizer = cv2.face.LBPHFaceRecognizer_create()
recognizer.read("face-trainner.yml")

labels = {"person_name": 1}
with open("pickles/face-labels.pickle", 'rb') as f:
    og_labels = pickle.load(f)
    labels = {v:k for k,v in og_labels.items()}

cap = cv2.VideoCapture(0)
check = 1
i_s = 2000

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()
    gray  = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.5, minNeighbors=5)
    for (x, y, w, h) in faces:
        #print(x,y,w,h)
        roi_gray = gray[y:y+h, x:x+w] #(ycord_start, ycord_end)
        roi_color = frame[y:y+h, x:x+w]

        # recognize? deep learned model predict keras tensorflow pytorch scikit learn
        id_, conf = recognizer.predict(roi_gray)
        if conf>=70 and conf <= 100:
            i_s = i_s + 1
            print(labels[id_])
            print(conf)
            font = cv2.FONT_HERSHEY_SIMPLEX
            img_item = "7.png"
            cv2.imwrite(img_item, roi_color)
            name = labels[id_]
            color = (255, 255, 255)
            stroke = 2
            cv2.putText(frame, name, (x,y), font, 1, color, stroke, cv2.LINE_AA)
            if labels[id_] == "lots_of_georgian_second":
                post_frame()
                time.sleep(1)

        color = (255, 0, 0) #BGR 0-255 
        stroke = 2
        end_cord_x = x + w
        end_cord_y = y + h
        check = 1
        if check == 1:
            cv2.rectangle(frame, (x, y), (end_cord_x, end_cord_y), color, stroke)
            check = 0
        #subitems = smile_cascade.detectMultiScale(roi_gray)
        #for (ex,ey,ew,eh) in subitems:
        #   cv2.rectangle(roi_color,(ex,ey),(ex+ew,ey+eh),(0,255,0),2)
    # Display the resulting frame
    cv2.imshow('frame',frame)
    if cv2.waitKey(20) & 0xFF == ord('q'):
        break

@app.route("/hello", methods=["GET"])
def hello():
    #os.system("shutdown /s /t 1")
    files = {
    "text":"hogehoge",
    "img": "asdasd"
    }
    return json.dumps(files)

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()

if __name__ == "__main__":
    app.debug = True
    app.run()
