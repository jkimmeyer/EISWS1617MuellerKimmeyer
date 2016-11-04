/*
 *  EISWS1617
 * 
 *  Proof of Concept - Server
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {

        app.route('/')

            /* * * * * * * * * * * * *
             *  Willkommensnachricht *
             * * * * * * * * * * * * */

            .get(function (req, res) {
                res.status(200).send("Server laeuft!");
            });

        app.route('/users')

            /* * * * * * * * * * *
             *  Liste aller User *
             * * * * * * * * * * */

            .get(function (req, res) {
                mongoose.model('users').find(function (err, users) {
                    var json = {"users": users};
                    res.json(json);
                });
            })

            /* * * * * * * * * * *
             *  User hinzufügen  *
             * * * * * * * * * * */

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

        app.route('/wasserwerte')

            /* * * * * * * * * * * * * *
             *  Wasserwerte ausgeben   *
             * * * * * * * * * * * * * */

            .get(function (req, res) {

                if (req.query.token == null) {
                    // Alle ausgeben
                    mongoose.model('wasserwerte').find(function (err, wasserwerte) {
                        var json = { "wasserwerte": wasserwerte };
                        res.json(json);
                    });
                }
                else {
                    var token = req.query.token;
                    mongoose.model('wasserwerte').find({ "token": token }, function (err, wasserwerte) {
                        if (wasserwerte.length > 0) {
                            // Wasserwerte gefunden
                            var json = { "wasserwerte": wasserwerte };
                            res.json(json);
                        }
                        else {
                            // Kein Eintrag
                            res.json({ "wasserwerte": [] });
                        }
                    });
                }
            })

            /* * * * * * * * * * * * * * *
             *  Wasserwerte verschicken  *
             * * * * * * * * * * * * * * */

            .post(function (req, res) {

                if (!req.body.tokens || !req.body.message) {
                    return res.status(400).json("Keine Token oder keine Nachricht angegeben!");
                }
                else {

                    var tokens = req.body.tokens;
                    var message = req.body.message;

                    // Wasserwerte in DB eintragen:

                    var insert = [{ "token": tokens, "ph": message.ph, "kalzium": message.kalzium }];

                    mongoose.model('wasserwerte').insertMany(insert, function (error, docs) {

                        if (error) {
                            console.log(error);
                        }
                        else {
                            console.log("Eingetragen!");
                        }

                    });

                    // Notification senden

                    var serverKey = 'AIzaSyBdxXLHWmA_E_b9dpRPdWIDaYPQ59dqLVE';
                    var fcm = new FCM(serverKey);

                    var message = {
                        to: tokens,
                        notification: {
                            title: 'Neue Wasserwerte',
                            body: 'Deine Fachhandlung hat dir soeben neue Wasserwerte zugeschickt!'
                        }
                    }

                    fcm.send(message, function (err, response) {
                        if (err) {
                            res.status(200).send(error);
                        }
                        else {
                            res.status(200).json(response);
                        }
                    });

                }

            });
        
    }
}