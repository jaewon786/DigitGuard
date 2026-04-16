const CONTENTS = [
  {
    id: '1',
    title: '허위 바이러스 경고 구별하기',
    summary: '진짜 경고와 가짜 광고를 구별하는 방법을 알아봅니다.',
    category: 'fake_ad',
    steps: [
      '진짜 바이러스 경고는 직접 설치한 백신 앱에서만 나옵니다.',
      '인터넷 창이나 광고에서 나오는 바이러스 경고는 모두 가짜입니다.',
      '"지금 설치하세요", "즉시 치료하세요"라는 문구가 있으면 가짜입니다.',
      '당황하지 말고 뒤로가기 버튼을 누르세요.',
      '모르겠으면 보호자에게 전화하세요.',
    ],
  },
  {
    id: '2',
    title: '의심스러운 앱 설치하지 않기',
    summary: '안전한 앱과 위험한 앱을 구별하는 방법을 배웁니다.',
    category: 'app_safety',
    steps: [
      '플레이스토어에서만 앱을 설치하세요.',
      '다운로드 수가 적고 리뷰가 없는 앱은 주의하세요.',
      '너무 많은 권한을 요구하는 앱은 위험할 수 있습니다.',
      '"무료 선물", "당첨"을 내세우는 앱은 사기일 가능성이 높습니다.',
      '잘 모르는 앱은 보호자에게 확인 후 설치하세요.',
    ],
  },
  {
    id: '3',
    title: '보이스피싱 대처법',
    summary: '전화 사기를 당하지 않는 방법을 알아봅니다.',
    category: 'voice_phishing',
    steps: [
      '검찰, 경찰, 은행은 전화로 돈을 요구하지 않습니다.',
      '"안전계좌로 이체하세요"라는 말은 100% 사기입니다.',
      '가족이 다쳤다는 전화가 오면 먼저 직접 확인하세요.',
      '모르는 번호의 전화는 받지 않아도 됩니다.',
      '의심되면 즉시 끊고 112에 신고하세요.',
    ],
  },
  {
    id: '4',
    title: '스미싱 문자 조심하기',
    summary: '문자 사기를 알아보는 방법을 배웁니다.',
    category: 'smishing',
    steps: [
      '택배 조회, 청첩장, 부고 문자의 링크를 누르지 마세요.',
      '은행, 카드사에서 보낸 문자라도 링크가 있으면 의심하세요.',
      '정부 지원금, 당첨 문자는 대부분 사기입니다.',
      '문자 속 전화번호로 전화하지 마세요.',
      '의심 문자는 삭제하고 보호자에게 알려주세요.',
    ],
  },
  {
    id: '5',
    title: '안전한 비밀번호 만들기',
    summary: '해킹을 방지하는 비밀번호 관리법을 배웁니다.',
    category: 'password',
    steps: [
      '비밀번호는 8자리 이상으로 만드세요.',
      '생년월일, 전화번호 같은 쉬운 숫자를 쓰지 마세요.',
      '모든 사이트에 같은 비밀번호를 쓰지 마세요.',
      '비밀번호를 다른 사람에게 알려주지 마세요.',
      '비밀번호가 유출되었다면 즉시 변경하세요.',
    ],
  },
];

exports.getContents = async (req, res, next) => {
  try {
    // 목록 조회 시에는 steps 제외
    const list = CONTENTS.map(({ steps, ...rest }) => rest);
    res.json({ contents: list, count: list.length });
  } catch (err) {
    next(err);
  }
};

exports.getContentById = async (req, res, next) => {
  try {
    const { id } = req.params;
    const content = CONTENTS.find((c) => c.id === id);
    if (!content) {
      return res.status(404).json({ error: '콘텐츠를 찾을 수 없습니다.' });
    }
    res.json(content);
  } catch (err) {
    next(err);
  }
};
