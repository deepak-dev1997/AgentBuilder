from flask import Flask, request

app = Flask(__name__)

@app.route("/salesLead", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
def create_lead():
    # 👉 Just log whatever the caller sent
    print("🔔 Incoming request -------------------------")
    print("Method :", request.method)
    print("Headers:", dict(request.headers))
    print("Query   :", request.args)        # e.g. /lead?foo=bar
    print("Form    :", request.form)        # x-www-form-urlencoded / multipart
    print("JSON    :", request.get_json(silent=True))
    print("Raw     :", request.data)        # bytes
    print("--------------------------------------------")

    return "Your lead has been created, thank you.", 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=6000, debug=True)
