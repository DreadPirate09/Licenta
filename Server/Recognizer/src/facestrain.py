import cv2
import os
import numpy as np
from PIL import Image
import pickle

def execute():
	BASE_DIR = os.path.dirname(os.path.abspath(__file__))
	image_dir = os.path.join(BASE_DIR, "images")
	print("The dir:")
	print(image_dir)

	face_cascade = cv2.CascadeClassifier('C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\cascades\\data\\haarcascade_frontalface_alt2.xml')
	recognizer = cv2.face.LBPHFaceRecognizer_create()


	clearConsole = lambda: os.system('cls' if os.name in ('nt', 'dos') else 'clear')


	current_id = 0
	label_ids = {}
	y_labels = []
	x_train = []
	failure = 0
	for root, dirs, files in os.walk(image_dir):
		for file in files:
			if file.endswith("png") or file.endswith("jpg") or file.endswith("jpeg"):
				path = os.path.join(root, file)
				#print(path)
				label = os.path.basename(root).replace(" ", "-").lower()
				#print(label, path)
				#print("Loading ");
				if not label in label_ids:
					label_ids[label] = current_id
					current_id += 1
				id_ = label_ids[label]
				#print(label_ids)
				#y_labels.append(label) # some number
				#x_train.append(path) # verify this image, turn into a NUMPY arrray, GRAY
				pil_image = Image.open(path).convert("L") # grayscale
				size = (550, 550)
				final_image = pil_image.resize(size, Image.ANTIALIAS)
				image_array = np.array(final_image, "uint8")
				#print(image_array)
				failure = failure + 1
				faces = face_cascade.detectMultiScale(image_array, scaleFactor=1.5, minNeighbors=5)

				for (x,y,w,h) in faces:
					failure = failure - 1
					#print("For this one the face was detected")
					roi = image_array[y:y+h, x:x+w]
					x_train.append(roi)
					y_labels.append(id_)

		print("the number of failures for this one " + str(failure))
		failure = 0


	#print(y_labels)
	#print(x_train)

	with open("C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\pickles\\face-labels.pickle", 'wb') as f:
		pickle.dump(label_ids, f)

	recognizer.train(x_train, np.array(y_labels))
	recognizer.save("face-trainner.yml")