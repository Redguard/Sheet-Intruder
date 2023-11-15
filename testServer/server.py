import os
from flask import Flask, flash, request, redirect, url_for, make_response
from werkzeug.utils import secure_filename
import datetime

ALLOWED_EXTENSIONS = {'xls', 'xlsx'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = "/output"


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # If the user does not select a file, the browser submits an
        # empty file without a filename.
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = secure_filename(datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S-") + file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return make_response(f"Received file of length {file.seek(0, os.SEEK_END)}")
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name=file>
      <input type=submit value=Upload>
    </form>
    '''



if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)