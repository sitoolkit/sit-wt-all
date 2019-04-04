window.language = {
  pageTitle: 'Input Page',
  weekDays: [
    'Sunday',
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday'
  ],
  addresses: {
    P01: ["札幌", "函館", "富良野"],
    P02: ["青森", "弘前", "八戸"],
    P03: ["秋田", "北秋田", "大仙"],
  },
  confirm: 'Are you sure ?',
  inputMessage: 'English input message',
  form: {
    headers: {
      name: 'Name',
      input: 'Form',
      description: 'Supported input operations',
    },
    items: [
      {
        id: 'name',
        label: 'Name',
        descriptions: [
          {
            text: 'Input to TextBox',
            children: [
              'Overwrite', 'Append'
            ]
          },
          {
            text: 'Javascriptのアラートを承諾<br />' +
              '(左の名前入力欄は「!"#$%&\'」のいずれかが含まれた状態でフォーカスアウトするとアラートを表示します。)'
          }
        ]
      },
      {
        id: 'gender',
        name: 'Gender',
        labels: {
          male: 'Male', female: 'Female',
        },
        descriptions: [
          {
            text: 'ラジオボタンの選択',
            children: [
              'input要素のid属性を指定して選択',
              'input要素のvalue属性を指定して選択',
              'label要素のtext文字列を指定して選択',
            ]
          }
        ]
      },
      {
        id: 'address',
        name: 'Address',
        labels: {
          prefecture: 'State', city: 'City',
        },
        descriptions: [
          {
            text: 'セレクトボックスの選択',
            children: [
              'option要素のvalue属性を指定して選択',
              'option要素のtext文字列を指定して選択',
              'option要素の順番を指定して選択',
            ]
          }
        ]
      },
    ]
  }
}