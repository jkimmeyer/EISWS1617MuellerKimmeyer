/*
 *  EISWS1617
 * 
 *  Server
 *
 *  (c) 2016 by Moritz Müller, Johannes Kimmeyer
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
    kalzium: Number
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