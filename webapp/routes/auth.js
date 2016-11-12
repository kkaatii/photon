'use strict';

var authEnabled = JSON.parse(process.env.ENABLE_AUTH);
var LOCAL_API_SERVER = process.env.LOCAL_API_SERVER;

module.exports = authEnabled ? function (req, res, next) {
  if (typeof req.user !== 'undefined' && validUser(req.user))
    next();
  else {
    req.logout();
    res.redirect('/login?returnTo=' + encodeURI(req.originalUrl));
  }
} : function (req, res, next) {
  req.user = {tube: {id: -1, nickname: 'MOCK OWNER'}};
  next();
};

// TODO split mfw auth and tube auth
function validUser(user) {
  return user.tube !== null;
  /*
   switch (user.nickname) {
   case "finn199411":
   user.name = "finn199411";
   return true;
   case "bulb.dan":
   case "evomjv":
   user.name = "evomjv";
   return true;
   default:
   return false;
   }*/
}