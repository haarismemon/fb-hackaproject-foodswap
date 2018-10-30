const Sequelize = require('sequelize');
// Heroku DB config
const sequelize = new Sequelize('heroku_d808d9b852a05ac', 'b6c156bfb47734', '98b17f54', {
    host: 'eu-cdbr-west-02.cleardb.net',

//Haaris' localhost DB config
//const sequelize = new Sequelize('foodswap', 'root', 'Haaris', {
//    host: 'localhost',
    port: 0000,
    dialect: 'mysql',
    pool: {
        min: 0,
        max: 5,
        idle: 10000,
        acquire: 30000,
    },
});


//DB connection test
sequelize.authenticate().then(function() {
    console.log("DB connected.");
}).catch(function(err) {
    console.log("DB connection failed. "+err);
});

module.exports = sequelize;