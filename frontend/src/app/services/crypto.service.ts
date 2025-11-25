import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment.development';
import CryptoJS from 'crypto-js';

@Injectable({
    providedIn: 'root'
})
export class CryptoService {

    secretKey = environment.secretKey

    encrypt(plainText: string): { encrypted: string; iv: string } {
        try {
            const iv = CryptoJS.lib.WordArray.random(128 / 8)
            const encrypted = CryptoJS.AES.encrypt(plainText, this.secretKey, {
                iv: iv,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            })

            return {
                encrypted: encrypted.toString(),
                iv: iv.toString()
            }
        } catch (error) {
            console.error("Error encrypting text", error);
            return {encrypted: "", iv: ""};
        }
    }

    decrypt(encryptedText: string, ivString: string): string {
        try {
            const iv = CryptoJS.enc.Hex.parse(ivString)
            const decrypted = CryptoJS.AES.decrypt(encryptedText, this.secretKey, {
                iv: iv,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            })

            return decrypted.toString(CryptoJS.enc.Utf8)
        } catch (error) {
            console.error("Error decrypting text", error);
            return "";
        }
    }

    generateHash(text: string): string {
        return CryptoJS.SHA256(text).toString()
    }

    generateMD5(text: string): string {
        return CryptoJS.MD5(text).toString()
    }

    generateRandomKey(length: number = 64): string {
        return CryptoJS.lib.WordArray.random(length).toString()
    }
}
