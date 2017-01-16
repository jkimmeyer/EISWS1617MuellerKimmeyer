/*
 *  EISWS1617
 * 
 *  Auqaapp - Server - Aquarien
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {
		
		app.route('/aquarien')

            /* * * * * * * * * * * * *
             *  Liste aller Aquarien *
             * * * * * * * * * * * * */

            .get(function (req, res) {
                mongoose.model('aquarien').find(function (err, aquarien) {
                    var json = {success: "true", aquarien: aquarien};
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
			
	}
}