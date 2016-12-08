/*
 *  EISWS1617
 * 
 *  Rapid Prototype - Server
 *
 *  Autor: Moritz Müller, Johannes Kimmeyer
 */

// Node Module einbinden

global.express = require('express');
global.bodyParser = require('body-parser');
global.mongoose = require('mongoose');
global.http = require('http');
global.FCM = require('fcm-node');

// Node Module initialisieren

global.app = express();
app.use(bodyParser.json());

// Zum lokalen Testen folgende Zeile auskommentieren und Zeile 25 entfernen!
//mongoose.connect('mongodb://localhost/fcm');

mongoose.connect("mongodb://eis1617_mongoadmin:ohcie9Choo@localhost:21045/fcm", { auth: { authdb: "admin" } });

// Einstellungen

app.set('port', process.env.PORT || 61000);

// Mongoose Models

var userSchema = mongoose.Schema({
    token: String
});
userSchema.set('collection', 'users');
mongoose.model('users', userSchema);

var wwSchema = mongoose.Schema({
    token: String,
    ph: Number,
    KH: Number,
    dailyUse: Number
});
wwSchema.set('collection', 'wasserwerte');
mongoose.model('wasserwerte', wwSchema);

// Routen

var routes = require('./routes/routes.js');
routes.init(app);

// App starten

var server = app.listen(app.get('port'), function () {

    var port = server.address().port;

    console.log('Server läuft auf Port %s', port);

});
