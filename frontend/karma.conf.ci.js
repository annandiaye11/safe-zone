// Karma configuration for CI/CD environments
// This config uses Puppeteer's bundled Chrome for reliable CI tests

const puppeteer = require('puppeteer');

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage')
    ],
    client: {
      jasmine: {
        random: false
      },
      clearContext: false
    },
    jasmineHtmlReporter: {
      suppressAll: true
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/frontend'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' }
      ]
    },
    reporters: ['progress', 'kjhtml'],
    browsers: ['ChromeHeadlessPuppeteer'],
    customLaunchers: {
      ChromeHeadlessPuppeteer: {
        base: 'ChromeHeadless',
        flags: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-web-security',
          '--disable-gpu',
          '--disable-dev-shm-usage',
          '--remote-debugging-port=9222'
        ]
      }
    },
    restartOnFileChange: false,
    singleRun: true
  });

  // Use Puppeteer's bundled Chrome in CI environments
  if (process.env.CI || process.env.JENKINS_URL) {
    process.env.CHROME_BIN = puppeteer.executablePath();
  }
};
