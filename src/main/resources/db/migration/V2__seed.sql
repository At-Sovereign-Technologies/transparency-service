INSERT INTO transparency_record (election_id, event_type, description, timestamp)
SELECT 
    e_id,

    (
        ARRAY[
            'ACTA_REGISTRADA',
            'RESULTADO_VALIDADO',
            'ACTA_CORREGIDA',
            'RECUENTO_SOLICITADO',
            'RECUENTO_FINALIZADO',
            'AUDITORIA_INICIADA',
            'AUDITORIA_COMPLETADA'
        ]
    )[floor(random()*7 + 1)],

    'Evento en mesa ' || (gs % 200),

    NOW() - (gs || ' minutes')::interval

FROM generate_series(1, 300) gs,
     generate_series(1, 5) e_id;
