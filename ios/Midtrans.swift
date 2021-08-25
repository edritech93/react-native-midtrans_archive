import Foundation
import MidtransKit

@objc(Midtrans)
class Midtrans: NSObject {
    
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    @objc(getConstants:withRejecter:)
    func getConstants(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve("123qwe")
    }
    
    @objc(checkout:withResolver:withRejecter:)
    func checkout(data: NSObject, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        MidtransMerchantClient.shared().requestTransactionToken(
            withTransactionDetails: transactionDetail,
            itemDetails: itemDetails,
            customerDetails: customerDetail) { [self] token, error in
            if let token = token {
                let vc = MidtransUIPaymentViewController(token: token)
                present(vc, animated: true)
            } else {
                // do something on error
            }
        }
        resolve(data)
    }
}
