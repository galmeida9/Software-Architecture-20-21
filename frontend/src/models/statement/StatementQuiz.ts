import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { ISOtoString } from '@/services/ConvertDateService';
const CryptoJS = require('crypto-js');
const Hex = require('crypto-js/enc-hex');
const Base64 = require('crypto-js/enc-base64');
const Latin1 = require('crypto-js/enc-latin1');
const AES = require('crypto-js/aes');

export default class StatementQuiz {
  id!: number;
  courseName!: string;
  quizAnswerId!: number;
  title!: string;
  qrCodeOnly!: boolean;
  oneWay!: boolean;
  timed!: boolean;
  availableDate!: string;
  conclusionDate!: string;
  timeToAvailability!: number | null;
  timeToSubmission!: number | null;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  username: String = '';
  private lastTimeCalled: number = Date.now();
  private timerId!: number;

  constructor(data: any) {
    if (data) {
      let key = Hex.parse(process.env.VUE_APP_AES_HEX_KEY);

      let iv = Base64.parse(data.iv);
      let rawData = Base64.parse(data.data);

      let plaintextData = AES.decrypt({ ciphertext: rawData }, key, {
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
        iv: iv
      });
      let plaintext = plaintextData.toString(Latin1);
      let jsonObj: StatementQuiz = JSON.parse(plaintext);

      this.id = jsonObj.id;
      this.courseName = jsonObj.courseName;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.title = jsonObj.title;
      this.qrCodeOnly = jsonObj.qrCodeOnly;
      this.oneWay = jsonObj.oneWay;
      this.timed = jsonObj.timed;
      this.availableDate = ISOtoString(jsonObj.availableDate);
      this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
      this.username = jsonObj.username;

      this.timeToAvailability = jsonObj.timeToAvailability;
      this.timeToSubmission = jsonObj.timeToSubmission;

      this.questions = jsonObj.questions.map(question => {
        return new StatementQuestion(question);
      });

      if (jsonObj.answers) {
        this.answers = jsonObj.answers.map(answer => {
          return new StatementAnswer(answer);
        });
      }

      // if there is timeTo... start an interval that decreases the timeTo... every second
      if (
        (this.timeToSubmission != null && this.timeToSubmission > 0) ||
        (this.timeToAvailability != null && this.timeToAvailability > 0)
      ) {
        this.timerId = setInterval(() => {
          if (this.timeToAvailability != null && this.timeToAvailability > 0) {
            this.timeToAvailability = Math.max(
              0,
              this.timeToAvailability -
                Math.floor(Date.now() - this.lastTimeCalled)
            );
          }

          if (this.timeToSubmission != null && this.timeToSubmission > 0) {
            this.timeToSubmission = Math.max(
              0,
              this.timeToSubmission -
                Math.floor(Date.now() - this.lastTimeCalled)
            );
          }

          if (!this.timeToSubmission && !this.timeToAvailability) {
            clearInterval(this.timerId);
          }

          this.lastTimeCalled = Date.now();
        }, 1000);
      }
    }
  }

  unansweredQuestions(): number {
    return this.answers.filter(x => !x.isQuestionAnswered()).length;
  }
}
