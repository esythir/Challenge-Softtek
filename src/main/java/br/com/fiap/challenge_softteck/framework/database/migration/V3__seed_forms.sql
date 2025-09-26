/* 1. FORMS SEEDING */
INSERT INTO TB_FORM (ID, CODE, NAME, FORM_TYPE, PERIODICITY_DAYS, REMINDER_DAYS, DESCRIPTION)
VALUES (SQ_FORM.NEXTVAL, 'CHECKIN',     'Check-in Diário',      'CHECKIN',        1, NULL,  'Responda diariamente como você se sentiu.');

INSERT INTO TB_FORM (ID, CODE, NAME, FORM_TYPE, PERIODICITY_DAYS, REMINDER_DAYS, DESCRIPTION)
VALUES (SQ_FORM.NEXTVAL, 'SELF_ASSESS', 'Autoavaliação',        'SELF_ASSESSMENT',7, 60, 'Questionário de riscos psicossociais.');

INSERT INTO TB_FORM (ID, CODE, NAME, FORM_TYPE, PERIODICITY_DAYS, REMINDER_DAYS, DESCRIPTION)
VALUES (SQ_FORM.NEXTVAL, 'CLIMATE',     'Diagnóstico de Clima', 'CLIMATE',        7, 90, 'Diagnóstico do clima organizacional.');

INSERT INTO TB_FORM (ID, CODE, NAME, FORM_TYPE, PERIODICITY_DAYS, REMINDER_DAYS, DESCRIPTION)
VALUES (SQ_FORM.NEXTVAL, 'REPORT',      'Canal de Escuta',      'REPORT',         0, NULL, 'Reporte ocorrências no ambiente de trabalho.');

-- ----------------------------------------------------------
-- 2. OPTIONS AND QUESTIONS SEEDING
DECLARE
    v_form_id        TB_FORM.ID%TYPE;
    v_q_id           TB_QUESTION.ID%TYPE;
    v_self_assess_id TB_FORM.ID%TYPE;
    v_climate_id     TB_FORM.ID%TYPE;
    v_report_id      TB_FORM.ID%TYPE;

    PROCEDURE add_choice_opts(
        p_question_id IN TB_OPTION.QUESTION_ID%TYPE,
        p_codes       IN SYS.ODCIVARCHAR2LIST,
        p_labels      IN SYS.ODCIVARCHAR2LIST
    ) IS
BEGIN
FOR i IN 1 .. p_codes.COUNT LOOP
            INSERT INTO TB_OPTION (ID, QUESTION_ID, ORDINAL, VALUE, LABEL)
            VALUES (SQ_OPTION.NEXTVAL, p_question_id, i, p_codes(i), p_labels(i));
END LOOP;
END add_choice_opts;
BEGIN

/* 3. CHECK-IN FORM */
SELECT ID INTO v_form_id FROM TB_FORM WHERE CODE = 'CHECKIN';

-- Q1
INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
VALUES (SQ_QUESTION.NEXTVAL, v_form_id, 1,
        'Escolha o seu emoji de hoje!', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_form_id AND ORDINAL = 1;
add_choice_opts(
        v_q_id,
        SYS.ODCIVARCHAR2LIST('TRISTE','ALEGRE','CANSADO','ANSIOSO','MEDO','RAIVA'),
        SYS.ODCIVARCHAR2LIST('Triste','Alegre','Cansado','Ansioso','Medo','Raiva')
    );

-- Q2
INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
VALUES (SQ_QUESTION.NEXTVAL, v_form_id, 2,
        'Como você se sente hoje?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_form_id AND ORDINAL = 2;
add_choice_opts(
        v_q_id,
        SYS.ODCIVARCHAR2LIST('MOTIVADO','CANSADO','PREOCUPADO','ESTRESSADO','ANIMADO','SATISFEITO'),
        SYS.ODCIVARCHAR2LIST('Motivado','Cansado','Preocupado','Estressado','Animado','Satisfeito')
    );


/* SELF-ASSESSMENT FORM */
SELECT ID INTO v_self_assess_id FROM TB_FORM WHERE CODE = 'SELF_ASSESS';

-- Q1..Q5
FOR ord IN 1..5 LOOP
        CASE ord
        WHEN 1 THEN
            INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
            VALUES (SQ_QUESTION.NEXTVAL, v_self_assess_id, ord,
                    'Como você avalia sua carga de trabalho?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_self_assess_id AND ORDINAL = ord;
add_choice_opts(
                v_q_id,
                SYS.ODCIVARCHAR2LIST('MUITO_LEVE','LEVE','MEDIA','ALTA','MUITO_ALTA'),
                SYS.ODCIVARCHAR2LIST('Muito Leve','Leve','Média','Alta','Muito Alta')
            );
WHEN 2 THEN
            INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
            VALUES (SQ_QUESTION.NEXTVAL, v_self_assess_id, ord,
                    'Sua carga de trabalho afeta sua qualidade de vida?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_self_assess_id AND ORDINAL = ord;
add_choice_opts(
                v_q_id,
                SYS.ODCIVARCHAR2LIST('NAO','RARAMENTE','AS_VEZES','FREQUENTEMENTE','SEMPRE'),
                SYS.ODCIVARCHAR2LIST('Não','Raramente','Às vezes','Frequentemente','Sempre')
            );
WHEN 3 THEN
            INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
            VALUES (SQ_QUESTION.NEXTVAL, v_self_assess_id, ord,
                    'Você trabalha além do seu horário regular?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_self_assess_id AND ORDINAL = ord;
add_choice_opts(
                v_q_id,
                SYS.ODCIVARCHAR2LIST('NAO','RARAMENTE','AS_VEZES','FREQUENTEMENTE','SEMPRE'),
                SYS.ODCIVARCHAR2LIST('Não','Raramente','Às vezes','Frequentemente','Sempre')
            );
WHEN 4 THEN
            INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
            VALUES (SQ_QUESTION.NEXTVAL, v_self_assess_id, ord,
                    'Você tem apresentado sintomas como insônia, irritabilidade ou cansaço extremo?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_self_assess_id AND ORDINAL = ord;
add_choice_opts(
                v_q_id,
                SYS.ODCIVARCHAR2LIST('NUNCA','RARAMENTE','AS_VEZES','FREQUENTEMENTE','SEMPRE'),
                SYS.ODCIVARCHAR2LIST('Nunca','Raramente','Às vezes','Frequentemente','Sempre')
            );
WHEN 5 THEN
            INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
            VALUES (SQ_QUESTION.NEXTVAL, v_self_assess_id, ord,
                    'Você sente que sua saúde mental prejudica sua produtividade no trabalho?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_self_assess_id AND ORDINAL = ord;
add_choice_opts(
                v_q_id,
                SYS.ODCIVARCHAR2LIST('NUNCA','RARAMENTE','AS_VEZES','FREQUENTEMENTE','SEMPRE'),
                SYS.ODCIVARCHAR2LIST('Nunca','Raramente','Às vezes','Frequentemente','Sempre')
            );
END CASE;
END LOOP;


/* CLIMATE FORM */
SELECT ID INTO v_climate_id FROM TB_FORM WHERE CODE = 'CLIMATE';

-- Q1..Q15
FOR rec IN (
      SELECT ROWNUM AS ord, txt
        FROM (
          SELECT 'Como está o seu relacionamento com seu chefe?' AS txt FROM dual UNION ALL
          SELECT 'Como está o seu relacionamento com seus colegas de trabalho?' FROM dual UNION ALL
          SELECT 'Sinto que sou tratado(a) com respeito pelos meus colegas de trabalho.' FROM dual UNION ALL
          SELECT 'Consigo me relacionar de forma saudável e colaborativa com minha equipe.' FROM dual UNION ALL
          SELECT 'Tenho liberdade para expressar minhas opiniões sem medo de retaliações.' FROM dual UNION ALL
          SELECT 'Me sinto acolhido(a) e parte do time onde trabalho.' FROM dual UNION ALL
          SELECT 'Sinto que existe espírito de cooperação entre os colaboradores.' FROM dual UNION ALL
          SELECT 'Recebo orientações claras e objetivas sobre minhas atividades e responsabilidades.' FROM dual UNION ALL
          SELECT 'Sinto que posso me comunicar abertamente com minha liderança.' FROM dual UNION ALL
          SELECT 'As informações importantes circulam de forma eficiente dentro da empresa.' FROM dual UNION ALL
          SELECT 'Tenho clareza sobre as metas e resultados esperados de mim.' FROM dual UNION ALL
          SELECT 'Minha liderança demonstra interesse pelo meu bem-estar no trabalho.' FROM dual UNION ALL
          SELECT 'Minha liderança está disponível para me ouvir quando necessário.' FROM dual UNION ALL
          SELECT 'Me sinto confortável para reportar problemas ou dificuldades ao meu líder.' FROM dual UNION ALL
          SELECT 'Minha liderança reconhece minhas entregas e esforços.' FROM dual UNION ALL
          SELECT 'Existe confiança e transparência na relação com minha liderança.' FROM dual
        )
    )
    LOOP
        INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
        VALUES (SQ_QUESTION.NEXTVAL, v_climate_id, rec.ord, rec.txt, 'SCALE');

SELECT ID INTO v_q_id
FROM TB_QUESTION
WHERE FORM_ID = v_climate_id
  AND ORDINAL = rec.ord;

add_choice_opts(
            v_q_id,
            SYS.ODCIVARCHAR2LIST('1','2','3','4','5'),
            SYS.ODCIVARCHAR2LIST('1','2','3','4','5')
        );
END LOOP;


/* REPORT FORM - LISTENING CHANNEL */
SELECT ID INTO v_report_id FROM TB_FORM WHERE CODE = 'REPORT';

-- Q1 – Report type
INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
VALUES (SQ_QUESTION.NEXTVAL, v_report_id, 1,
        'Qual tipo de ocorrência deseja reportar?', 'CHOICE');
SELECT ID INTO v_q_id FROM TB_QUESTION
WHERE FORM_ID = v_report_id AND ORDINAL = 1;
add_choice_opts(
        v_q_id,
        SYS.ODCIVARCHAR2LIST(
            'ASSÉDIO_MORAL',
            'ASSÉDIO_SEXUAL',
            'DISCRIMINAÇÃO_RACIAL',
            'DISCRIMINAÇÃO_DE_GÊNERO',
            'VIOLÊNCIA_FÍSICA',
            'VIOLÊNCIA_VERBAL',
            'CONFLITO_INTERPESSOAL',
            'SAÚDE_E_SEGURANÇA',
            'INFRAESTRUTURA_INADEQUADA',
            'EQUIPAMENTO_QUEBRADO',
            'ERGONOMIA_INADEQUADA',
            'OUTRO'
        ),
        SYS.ODCIVARCHAR2LIST(
            'Assédio Moral',
            'Assédio Sexual',
            'Discriminação Racial',
            'Discriminação de Gênero',
            'Violência Física',
            'Violência Verbal',
            'Conflito Interpessoal',
            'Assuntos de Saúde e Segurança',
            'Infraestrutura Inadequada',
            'Equipamento Quebrado',
            'Ergonomia Inadequada',
            'Outro'
        )
    );

-- Q2 – Report description
INSERT INTO TB_QUESTION(ID, FORM_ID, ORDINAL, TEXT, QTYPE)
VALUES (SQ_QUESTION.NEXTVAL, v_report_id, 2,
        'Descreva a ocorrência (opcional):', 'TEXT');

END;
/
