import { NativeModules } from 'react-native';

type MidtransType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Midtrans } = NativeModules;

export default Midtrans as MidtransType;
