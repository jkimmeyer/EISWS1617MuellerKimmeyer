/*
 *  EISWS1617
 * 
 *  Rapid Prototype - Server
 *
 *  Autor: Moritz Müller, Johannes Kimmeyer
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
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').find({"uid": uid}, function (err, users) {
                        if (users.length > 0) {
                            // UID existiert bereits
                            res.json({success: "false"});
                        }
                        else {
							// Short ID generieren / Aktuell wird noch nicht sicher gestellt, dass die SID eindeutig ist
							var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
							
							var sid = "";
							for( var i=0; i < 4; i++ ){
								sid += possible.charAt(Math.floor(Math.random() * possible.length));
							}
							
                            // User eintragen
                            mongoose.model('users').insertMany([{ "uid": uid, "sid": sid, "token": token }], function (error, docs) {

                                if (error) {
                                    res.json(error);
                                }
                                else {
                                    res.json({ success: "true", sid: sid });
                                }

                            });
                            
                        }
                    });
                }
            })
			
			.put(function (req, res) {
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').findOneAndUpdate({'uid':uid}, { $set: { token: token }}, {upsert:true}, function(err, doc){
						if(err){
							return res.status(500).json({error: err});	
						}
						else{
							res.json({success: "true", sid: doc.sid});
						}
					});
                }
            });
		
		/* * * * * * * * * * * * * * * * * *
		 *  User anhand der SID ermitteln  *
		 * * * * * * * * * * * * * * * * * */
			
		app.route('/users/:sid')

            .get(function (req, res) {
				
                var sid = req.params.sid;
				
                mongoose.model('users').find({"sid": sid}, function (err, user) {
					
					if(err){
						return res.status(500).json({ success: "false" });	
					}
					else{
						var json = {success: "true", user: user};
						res.status(200).json(json);
					}
					
                });
				
            });
			
		app.route('/aquarien')

            /* * * * * * * * * * * * *
             *  Liste aller Aquarien *
             * * * * * * * * * * * * */

            .get(function (req, res) {
                mongoose.model('aquarien').find(function (err, aquarien) {
                    var json = {"aquarien": aquarien};
                    res.json(json);
                });
            })

            /* * * * * * * * * * * * *
             *  Aquarium hinzufügen  *
             * * * * * * * * * * * * */

            .post(function (req, res) {
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').find({"uid": uid}, function (err, users) {
                        if (users.length > 0) {
                            // UID existiert bereits
                            res.json({success: "false"});
                        }
                        else {
							// Short ID generieren / Aktuell wird noch nicht sicher gestellt, dass die SID eindeutig ist
							var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
							
							var sid = "";
							for( var i=0; i < 4; i++ ){
								sid += possible.charAt(Math.floor(Math.random() * possible.length));
							}
							
                            // User eintragen
                            mongoose.model('users').insertMany([{ "uid": uid, "sid": sid, "token": token }], function (error, docs) {

                                if (error) {
                                    res.json(error);
                                }
                                else {
                                    res.json({ success: "true", sid: sid });
                                }

                            });
                            
                        }
                    });
                }
            })
			
			.put(function (req, res) {
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').findOneAndUpdate({'uid':uid}, { $set: { token: token }}, {upsert:true}, function(err, doc){
						if(err){
							return res.status(500).json({error: err});	
						}
						else{
							res.json({success: "true", sid: doc.sid});
						}
					});
                }
            });	
			
		/* * * * * * * * * * * * * * * * * * * * *
		 *  Aquarien eines bestimmten Benutzers  *
		 * * * * * * * * * * * * * * * * * * * * */
			
		app.route('/aquarien/:user')
		
            .get(function (req, res) {
				
				var user = req.params.user;
				
                mongoose.model('aquarien').find({"userUID": user}, function (err, aquarien) {
					
					if(err){
						return res.status(500).json({ success: "false" });	
					}
					else{
						var json = {success: "true", aquarien: aquarien};
						res.status(200).json(json);
					}
					
                });
            })

            /* * * * * * * * * * * * *
             *  Aquarium hinzufügen  *
             * * * * * * * * * * * * */

            .post(function (req, res) {
				
				if (!req.body.bezeichnung || !req.body.laenge || !req.body.breite || !req.body.hoehe || !req.body.glasstaerke || !req.body.kieshoehe || !req.body.fd) {
                    return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
                }
                else {
                    var bezeichnung = req.body.bezeichnung;
					var laenge = req.body.laenge;
					var breite = req.body.breite;
					var hoehe = req.body.hoehe;
					var glasstaerke = req.body.glasstaerke;
					var kieshoehe = req.body.kieshoehe;
					var fd = req.body.fd;
					var uid = req.params.user;
                    // User eintragen
					mongoose.model('aquarien').insertMany([{ "userUID": uid, "bezeichnung": bezeichnung, "laenge": laenge, "breite": breite, "hoehe": hoehe, "glasstaerke": glasstaerke, "kieshoehe": kieshoehe, "fd": fd }], function (error, docs) {

						if (error) {
							res.json({ success: "false", error: error });
						}
						else {
							res.json({ success: "true" });
						}

					});
                }
            })
			
			/* * * * * * * * * * * * *
             *  Aquarium bearbeiten  *
             * * * * * * * * * * * * */
			
			.put(function (req, res) {
				
				if (!req.body.bezeichnung || !req.body.laenge || !req.body.breite || !req.body.hoehe || !req.body.glasstaerke || !req.body.kieshoehe || !req.body.fd) {
                    return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
                }
                else {
                    var bezeichnung = req.body.bezeichnung;
					var laenge = req.body.laenge;
					var breite = req.body.breite;
					var hoehe = req.body.hoehe;
					var glasstaerke = req.body.glasstaerke;
					var kieshoehe = req.body.kieshoehe;
					var fd = req.body.fd;
					var uid = req.params.user;
                    mongoose.model('aquarien').findOneAndUpdate({'userUID':uid}, { $set: { bezeichnung: bezeichnung, laenge: laenge, breite: breite, hoehe: hoehe, glasstaerke: glasstaerke, kieshoehe: kieshoehe, fd: fd }}, {upsert:true}, function(err, doc){
						if(err){
							return res.status(500).json({ success: "false", message: err});	
						}
						else{
							res.json({success: "true"});
						}
					});
                }
            });		

        app.route('/wasserwerte')

            /* * * * * * * * * * * * * * * * * * * * 
             *  Wasserwerte aller Nutzer ausgeben  *
             * * * * * * * * * * * * * * * * * * * */

            .get(function (req, res) {
				
				/*
				mongoose.model('wasserwerte').remove({}, function (err, doc){
					res.json({ success: "true" });
				});
				*/
				
                mongoose.model('wasserwerte').find(function (err, wasserwerte) {
					var json = { success: "true", wasserwerte: wasserwerte };
					res.json(json);
				});
				
            });
			
		/* * * * * * * * * * * * * * * * * * * * * *
		 *  Wasserwerte eines bestimmten Benutzers *
		 * * * * * * * * * * * * * * * * * * * * * */
			
		app.route('/wasserwerte/:user')
		
			/* * * * * * * * * * * * * *
             *  Wasserwerte ausgeben   *
             * * * * * * * * * * * * * */
		
			.get(function (req, res) {
				
				var user = req.params.user;
				
				mongoose.model('wasserwerte').find({ "userUID": user }, function (err, wasserwerte) {
					if (wasserwerte.length > 0) {
						// Wasserwerte gefunden
						var json = { success: "true", wasserwerte: wasserwerte };
						res.json(json);
					}
					else {
						// Kein Eintrag
						res.json({ success: "true", wasserwerte: [] });
					}
				});
				
			})
			
			/* * * * * * * * * * * * * *
             *  Wasserwerte eintragen  *
             * * * * * * * * * * * * * */

            .post(function (req, res) {

                if (!req.body.von || !req.body.datum || !req.body.kh || !req.body.gh || !req.body.ph || !req.body.co2 || !req.body.eisen || !req.body.kalium || !req.body.no3 || !req.body.po3) {
                    return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
                }
                else {
					
					var user = req.params.user;

                    var von = req.body.von;
                    var datum = req.body.datum;
					var kh = req.body.kh;
					var gh = req.body.gh;
					var ph = req.body.ph;
					var co2 = req.body.co2;
					var eisen = req.body.eisen;
					var kalium = req.body.kalium;
					var no3 = req.body.no3;
					var po3 = req.body.po3;

                    // Wasserwerte in DB eintragen:
					
                    var insert = [{ "userUID": user, "von": von, "datum": datum, "kh": kh, "gh": gh, "ph": ph, "co2": co2, "eisen": eisen, "kalium": kalium, "no3": no3, "po3": po3}];

                    mongoose.model('wasserwerte').insertMany(insert, function (error, docs) {

                        if (error) {
							
							// Wenn die Werte vom Benutzer eingetragen wurden, wird die Methode hier beendet.
							// Wenn sie von der Fachhandlung abgeschickt wurden, wird nur ein Log geschrieben
							// und weiter unten wird zusätzlich eine Benachrichtigung über das FCM an den
							// Nutzer geschickt
							
							if(von == "Benutzer"){
								return res.status(500).json({ success: "false", message: error });	
							}
							else{
                            	console.log(error);
							}
							
                        }
                        else {
                            if(von == "Benutzer"){
								return res.status(200).json({ success: "true" });	
							}
                        }

                    });
					
					if(von == "Fachhandlung"){

						// Notification senden
	
						var serverKey = 'AAAArt8gDDE:APA91bHbqepf4VNJKHQAUp8XY2yyTt3ntuB-s1tUII2ihwCWyhIDJmwTfali7bu8jOwAd7hMKhr7zbS9hO_VkuF9aU9Yok8Du0lb5ZgEXZdfDW_jAYDA6AM7FoeE8RgNMuNXcpxpaWXH35u3hhSiymvosvdfb2tknA';
						var fcm = new FCM(serverKey);
						
						// Token aus Datenbank lesen
						
						mongoose.model('users').find({ "uid": user }, function (err, ergebnis) {
							if (ergebnis.length > 0) {
								// User gefunden
								var token = ergebnis[0].token;
								
								var message = {
									to: token,
									notification: {
										title: 'Neue Wasserwerte',
										body: 'Deine Fachhandlung hat dir soeben neue Wasserwerte zugeschickt!'
									}
								}
			
								fcm.send(message, function (err, response) {
									if (err) {
										res.status(500).json({ success: "false", message: "Fehler beim Senden der Notification!", error: err });
									}
									else {
										res.status(200).json({ success: "true" });
									}
								});
								
							}
							else {
								// Kein User gefunden
								return res.status(500).json({ success: "false", message: "Es wurde kein Benutzer mit der UID gefunden!" });
							}
						});
						
					}

                }

            })
			
			/* * * * * * * * * * * * * *
             *  Wasserwerte löschen   *
             * * * * * * * * * * * * * */
		
			.delete(function (req, res) {
				
				if(!req.body.datum){
					return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
				}
				else{
				
					var user = req.params.user;
					var datum = req.body.datum;
					
					mongoose.model('wasserwerte').find({ "userUID": user, "datum": datum }).remove(function (err, doc){
						if(err){
							return res.status(500).json({ success: "false", message: err });	
						}
						else{
							return res.status(200).json({ success: "true" });	
						}
						
					});
					
				}
				
			});
        
    }
}
