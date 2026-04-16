-- DigitGuard 초기 위험 패턴 시드 데이터
-- docker-compose 시 자동 실행됨

INSERT INTO threat_patterns (pattern, pattern_type, threat_level, category) VALUES
-- 허위 바이러스 경고
('바이러스.*발견', 'regex', 'high', 'fake_virus'),
('바이러스.*감염', 'regex', 'high', 'fake_virus'),
('악성코드.*노출', 'regex', 'high', 'fake_virus'),
('악성코드.*발견', 'regex', 'high', 'fake_virus'),
('핸드폰.*감염', 'regex', 'high', 'fake_virus'),
('기기.*감염', 'regex', 'high', 'fake_virus'),
('지금.*치료', 'regex', 'high', 'fake_virus'),
('즉시.*제거', 'regex', 'high', 'fake_virus'),
('배터리.*손상', 'regex', 'medium', 'fake_virus'),
('성능.*저하.*감지', 'regex', 'medium', 'fake_virus'),
('메모리.*부족.*위험', 'regex', 'medium', 'fake_virus'),

-- 허위 보안 경고
('개인정보.*유출', 'regex', 'high', 'fake_security'),
('보안.*앱.*설치', 'regex', 'medium', 'fake_security'),
('긴급.*업데이트', 'regex', 'medium', 'fake_security'),
('기기.*해킹', 'regex', 'high', 'fake_security'),
('보안.*위협.*감지', 'regex', 'high', 'fake_security'),
('계정.*도용', 'regex', 'high', 'fake_security'),
('비밀번호.*노출', 'regex', 'high', 'fake_security'),

-- 피싱
('계좌.*정지', 'regex', 'high', 'phishing'),
('본인.*확인.*링크', 'regex', 'high', 'phishing'),
('택배.*조회.*클릭', 'regex', 'medium', 'phishing'),
('당첨.*축하', 'regex', 'high', 'phishing'),
('무료.*쿠폰.*지급', 'regex', 'medium', 'phishing'),
('카드.*승인.*취소', 'regex', 'high', 'phishing'),
('대출.*승인.*완료', 'regex', 'high', 'phishing'),
('정부.*지원금.*신청', 'regex', 'medium', 'phishing'),

-- 보이스피싱
('검찰.*수사관', 'regex', 'high', 'voice_phishing'),
('금융감독원', 'regex', 'high', 'voice_phishing'),
('계좌.*이체.*요청', 'regex', 'high', 'voice_phishing'),
('안전.*계좌.*이동', 'regex', 'high', 'voice_phishing')
ON CONFLICT DO NOTHING;
