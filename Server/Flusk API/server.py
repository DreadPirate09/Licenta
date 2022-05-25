from flask import Flask, jsonify, request, render_template
from PIL import Image
import json
import base64
from io import BytesIO
import numpy as np
import os
import cv2
import sys
sys.path.append('C:\\GitRepo1\\Licenta\\Licenta\\Server\\OpenCV-Python-Series-master\\src')
import faces


app = Flask(__name__)
count = 0

@app.route("/", methods=["GET", "POST"])
def index():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    img = dict_data["img"] 
    
    # text = dict_data["text"] + "fuga" #Properly process with the acquired text

    response = {
        "text":1,
        "img_data":1        
        }

    img = base64.b64decode(img)
    data_np = np.fromstring(img, dtype='uint8')
    decimg = cv2.imdecode(data_np, 1)
    cv2.imwrite("new.png", decimg)

    # here 

    return jsonify(response)

@app.route("/android", methods=["GET", "POST"])
def android():
    json_data = request.get_json() #Get the POSTed json

    response = {
        "code":100     
        }
    return jsonify(response)



if __name__ == "__main__":
    app.debug = True
    app.run()