// const Sequelize = require('sequelize-oracle');
// const Sequelize = require('cu8-sequelize-oracle');
const Sequelize = require('sequelize');


const sequelize = new Sequelize('foodswap', 'swap', '12345', {
    host: 'localhost',
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

