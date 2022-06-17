import RPi.GPIO as GPIO
from flask import Flask, jsonify, request

app = Flask(__name__)


def on():
    channel = 18
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(channel, GPIO.OUT)

def off():
    GPIO.cleanup()



@app.route("/unlockPi",methods=["GET","POST"])
def unlock():
    on()
    
    response = {
        "unlock":True
        }
    
    print('ok, unlock')
    
    return jsonify(response)

@app.route("/lockPi",methods=["GET","POST"])
def lock():
    off()
    
    response = {
        "unlock":True
        }
    
    print('ok, lock')
    
    return jsonify(response)

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=4040)
