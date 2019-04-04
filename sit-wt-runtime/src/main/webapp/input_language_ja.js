window.language = {
  pageTitle: '入力ページ',
  weekDays: '日月火水木金土',
  addresses: {
    P01: ["札幌", "函館", "富良野"],
    P02: ["青森", "弘前", "八戸"],
    P03: ["秋田", "北秋田", "大仙"],
  },
  confirm: 'よろしいですか？',
  inputMessage: 'このページではフォームの入力操作を確認します。 「利用規約」リンクをクリックすると利用規約ページが別ウィンドウで開きます。' +
    '「同意して確定」ボタンをクリックすると確認ダイアログが表示されます。 ダイアログを承諾すると完了ページに遷移します。拒否するとこのページに留まります。',
  form: {
    headers: {
      name: '入力項目名',
      input: '入力項目',
      description: 'サポートされる入力操作',
    },
    items: [
      {
        id: 'name',
        name: '名前',
        descriptions: [
          {
            text: 'テキストボックスの入力',
            children: [
              '上書', '追記'
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
        name: '性別',
        labels: {
          male: '男性', female: '女性',
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
        name: '住所',
        labels: {
          prefecture: '都道府県', city: '市区町村',
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