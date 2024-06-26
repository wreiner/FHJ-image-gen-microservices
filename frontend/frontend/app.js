var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var detailsRouter = require('./routes/details');
var submitRouter = require('./routes/submit');
var listRouter = require('./routes/list');

var app = express();

// image-gen API URL
IMAGE_GEN_API_URL = process.env.IMAGE_GEN_API_URL || "https://imagegen.wreiner.at";

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/', detailsRouter);
app.use('/', submitRouter);
app.use('/', listRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

var port = process.env.PORT || '3000';
var host = process.env.HOST || '0.0.0.0';

app.listen(port, host, function() {
  console.log('Express server listening on port ' + port);
});

module.exports = app;
