from flask import Flask, jsonify, request, render_template
import requests
from PIL import Image
import json
import base64
from io import BytesIO
import numpy as np
import os
import cv2
import sys
sys.path.append('C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src')
import faces
import facestrain

app = Flask(__name__)
ALLOW = 0
PERSON = "unknown"
count = 0
newAddedImages = 0

@app.route("/", methods=["GET", "POST"])
def index():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    img = dict_data["img"] 

    img = base64.b64decode(img)
    data_np = np.fromstring(img, dtype='uint8')
    decimg = cv2.imdecode(data_np, 1)
    cv2.imwrite("new.png", decimg)
    img = cv2.imread("new.png")
    ret2 = faces.execute(img)

    if ret2 == "Georgian":
        ALLOW = 1
        PERSON = ret2

    response = {
        "text":1,
        "img_data":1,
        "ret_val":ret2    
        }

    return jsonify(response)

@app.route("/addImages", methods=["GET", "POST"])
def addImages():
    json_data = request.get_json() #Get the POSTed json
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary


    img = dict_data["img"] 
    directoryReq = dict_data["directory"]
    print(directoryReq)

    img = base64.b64decode(img)
    data_np = np.fromstring(img, dtype='uint8')
    decimg = cv2.imdecode(data_np, 1)
    print(dict_data["nr"])
    directory = "C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\images\\"+str(directoryReq)
    nameImg ="\\new" + str(dict_data["nr"]) + ".jpg"
    cv2.imwrite(directory+nameImg, decimg)

    response = {
        "img":"added",
        "number img ":dict_data["nr"]    
        }

    return jsonify(response)

@app.route("/createFolderName", methods=["GET", "POST"])
def createFolderName():
    json_data = request.get_json() #Get the POSTed json
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    directory = dict_data["name"] 
    parent_dir = "C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\images"
    path = os.path.join(parent_dir, directory)
    os.mkdir(path)

    response = {
        "create":"success"    
        }

    return jsonify(response)

@app.route("/trainModel", methods=["GET", "POST"])
def trainModel():
    json_data = request.get_json() #Get the POSTed json
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    facestrain.execute()

    response = {
        "trained":"success"    
        }

    return jsonify(response)

@app.route("/unlock", methods=["GET", "POST"])
def unlock():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    jsonVal = {
    "text":"okSir",
    "action":"unlock",
    "person":PERSON
    }

    r = requests.post("http://192.168.0.107:4040/unlockPi", json=json.dumps(jsonVal))

    response = {
        "text":1,
        "img_data":1,
        "ret_val":1    
        }

    return jsonify(response)

@app.route("/lock", methods=["GET", "POST"])
def lock():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    jsonVal = {
    "text":"okSir",
    "action":"lock",
    "person":PERSON
    }

    r = requests.post("http://192.168.0.107:4040/lockPi", json=json.dumps(jsonVal))

    response = {
        "text":1,
        "img_data":1,
        "ret_val":1    
        }

    return jsonify(response)

@app.route("/unlockPassword", methods=["GET", "POST"])
def unlockPasssword():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    passHashRecived = dict_data['md5']

    if passHashRecived == '81dc9bdb52d04dc20036dbd8313ed055' :
        jsonVal = {
        "text":"okSir",
        "action":"unlock",
        "person":PERSON
        }
        r = requests.post("http://192.168.0.107:4040/unlockPi", json=json.dumps(jsonVal))
        response = {
        "text":"correct password",
        "img_data":1,
        "ret_val":1    
        }
    else :
        response = {
        "text":"wrong password",
        "img_data":1,
        "ret_val":1    
        }


    return jsonify(response)

@app.route("/lockPassword", methods=["GET", "POST"])
def lockPasssword():
    json_data = request.get_json() #Get the POSTed json
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    print(dict_data)

    passHashRecived = dict_data['md5']

    if passHashRecived == '81dc9bdb52d04dc20036dbd8313ed055' :
        jsonVal = {
        "text":"okSir",
        "action":"lock",
        "person":PERSON
        }
        r = requests.post("http://192.168.0.107:4040/lockPi", json=json.dumps(jsonVal))
        response = {
        "text":"correct password",
        "img_data":1,
        "ret_val":1    
        }
    else :
        response = {
        "text":"wrong password",
        "img_data":1,
        "ret_val":1    
        }

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