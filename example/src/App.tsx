import * as React from 'react';
import { StyleSheet, View, Button } from 'react-native';
import Midtrans from 'react-native-midtrans';

export default function App() {
  React.useEffect(() => {
    console.log(
      'Midtrans.getConstants()',
      Midtrans.getConstants()
    );
  }, []);

  return (
    <View style={styles.container}>
      <Button
        onPress={() => {
          Midtrans.checkout({
            order_id: Math.random().toString(),
            client_key: '',
            merchant_base_url: '',
            items: [
              {
                id: Math.random().toString(),
                price: 100000,
                quantity: 2,
                name: 'Items',
              },
            ],
          })
            .then((result) => {
              console.log('result', result);
            })
            .catch((err) => {
              console.log('err', err);
            });
        }}
        title={'test'}
      >
        test checkout
      </Button>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
