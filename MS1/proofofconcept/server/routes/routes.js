module.exports = {
    init: function (app) {

        app.route('/')

            .get(function (req, res) {
                mongoose.model('users').find(function (err, users) {
                    res.send(users);
                });
            })

            .post(function (req, res) {
                if (!req.body.Token) {
                    return res.status(400).json("Kein Token angegeben!");
                }
                else {
                    var token = req.body.Token;
                    mongoose.model('users').find({"token": token}, function (err, users) {
                        if (users.length > 0) {
                            // Token existiert bereits
                            res.send(users);
                        }
                        else {
                            // Token eintragen
                            mongoose.model('users').insertMany([{ "token": token }], function (error, docs) {

                                if (error) {
                                    res.send(error);
                                }
                                else {
                                    res.send("Eingetragen!");
                                }

                            });
                            
                        }
                    });
                }
            });
    }
}