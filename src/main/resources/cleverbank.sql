PGDMP     6    !                {         
   cleverbank    15.4    15.3 !    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16398 
   cleverbank    DATABASE     �   CREATE DATABASE cleverbank WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1251';
    DROP DATABASE cleverbank;
                postgres    false            �           0    0    DATABASE cleverbank    COMMENT     @   COMMENT ON DATABASE cleverbank IS 'database for CleverBankApp';
                   postgres    false    3267            �            1259    16533    accounts    TABLE     �   CREATE TABLE public.accounts (
    id integer NOT NULL,
    balance double precision,
    client_id bigint,
    bank_id bigint,
    date_open timestamp without time zone
);
    DROP TABLE public.accounts;
       public         heap    postgres    false            �            1259    16532    accounts_id_seq    SEQUENCE     �   CREATE SEQUENCE public.accounts_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.accounts_id_seq;
       public          postgres    false    218            �           0    0    accounts_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.accounts_id_seq OWNED BY public.accounts.id;
          public          postgres    false    217            �            1259    16516    banks    TABLE     X   CREATE TABLE public.banks (
    id integer NOT NULL,
    name character varying(255)
);
    DROP TABLE public.banks;
       public         heap    postgres    false            �            1259    16515    banks_id_seq    SEQUENCE     �   CREATE SEQUENCE public.banks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.banks_id_seq;
       public          postgres    false    215            �           0    0    banks_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.banks_id_seq OWNED BY public.banks.id;
          public          postgres    false    214            �            1259    16522    clients    TABLE     Y   CREATE TABLE public.clients (
    id bigint NOT NULL,
    name character varying(255)
);
    DROP TABLE public.clients;
       public         heap    postgres    false            �            1259    16545    transactions    TABLE     �   CREATE TABLE public.transactions (
    id integer NOT NULL,
    sender_account_id bigint,
    receiver_account_id bigint,
    amount double precision,
    "timestamp" timestamp without time zone,
    trans_type character varying(10)
);
     DROP TABLE public.transactions;
       public         heap    postgres    false            �            1259    16544    transactions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.transactions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.transactions_id_seq;
       public          postgres    false    220            �           0    0    transactions_id_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE public.transactions_id_seq OWNED BY public.transactions.id;
          public          postgres    false    219                       2604    16536    accounts id    DEFAULT     j   ALTER TABLE ONLY public.accounts ALTER COLUMN id SET DEFAULT nextval('public.accounts_id_seq'::regclass);
 :   ALTER TABLE public.accounts ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    217    218    218                       2604    16519    banks id    DEFAULT     d   ALTER TABLE ONLY public.banks ALTER COLUMN id SET DEFAULT nextval('public.banks_id_seq'::regclass);
 7   ALTER TABLE public.banks ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    214    215    215                       2604    16548    transactions id    DEFAULT     r   ALTER TABLE ONLY public.transactions ALTER COLUMN id SET DEFAULT nextval('public.transactions_id_seq'::regclass);
 >   ALTER TABLE public.transactions ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    220    219    220            �          0    16533    accounts 
   TABLE DATA                 public          postgres    false    218   �"       �          0    16516    banks 
   TABLE DATA                 public          postgres    false    215   �#       �          0    16522    clients 
   TABLE DATA                 public          postgres    false    216   $       �          0    16545    transactions 
   TABLE DATA                 public          postgres    false    220   �$       �           0    0    accounts_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.accounts_id_seq', 10, true);
          public          postgres    false    217            �           0    0    banks_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.banks_id_seq', 5, true);
          public          postgres    false    214            �           0    0    transactions_id_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.transactions_id_seq', 141, true);
          public          postgres    false    219            "           2606    16538    accounts accounts_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.accounts DROP CONSTRAINT accounts_pkey;
       public            postgres    false    218                       2606    16521    banks banks_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.banks
    ADD CONSTRAINT banks_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.banks DROP CONSTRAINT banks_pkey;
       public            postgres    false    215                        2606    16526    clients clients_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.clients DROP CONSTRAINT clients_pkey;
       public            postgres    false    216            $           2606    16550    transactions transactions_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.transactions DROP CONSTRAINT transactions_pkey;
       public            postgres    false    220            %           2606    16566     accounts accounts_client_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.clients(id);
 J   ALTER TABLE ONLY public.accounts DROP CONSTRAINT accounts_client_id_fkey;
       public          postgres    false    218    216    3104            &           2606    16561    accounts fk_bank    FK CONSTRAINT     o   ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT fk_bank FOREIGN KEY (bank_id) REFERENCES public.banks(id);
 :   ALTER TABLE ONLY public.accounts DROP CONSTRAINT fk_bank;
       public          postgres    false    3102    218    215            '           2606    16556 2   transactions transactions_receiver_account_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_receiver_account_id_fkey FOREIGN KEY (receiver_account_id) REFERENCES public.accounts(id);
 \   ALTER TABLE ONLY public.transactions DROP CONSTRAINT transactions_receiver_account_id_fkey;
       public          postgres    false    220    218    3106            (           2606    16551 0   transactions transactions_sender_account_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.transactions
    ADD CONSTRAINT transactions_sender_account_id_fkey FOREIGN KEY (sender_account_id) REFERENCES public.accounts(id);
 Z   ALTER TABLE ONLY public.transactions DROP CONSTRAINT transactions_sender_account_id_fkey;
       public          postgres    false    218    3106    220            �   �   x��ұ
�@�O��
ג�w�Z'��`�{�I��o�vr!�G��� m7�9]nא�!<�����������^�k ed��g\��Vu�9��JN�ۤ�Ctر��W7���FE,�$[K�*"�H�F��%�8GS��~���uD��h��'�ARF&�������q�(      �   f   x���v
Q���W((M��L�KJ��.Vs�	uV�0�QPw�I-K-�uJ�kZsy�c����D�Zc�ڠ�Ҵ�̪�<��0j�"^�)P�k(\= �H      �   �   x�����@��>��!��W�D�%��z�H'֮L���v���̗�s�M��en�-�'�y+WL�*�0����&�"ShHo\��4���E3{ p�4����K8/��ż|]߅$��B|r�	��3$(k�x¡�1�3����7*!&�4c7
�2|+#�jb�ΜH*�0��G촭\�������/����      �   /  x���K�5���sH��v5�H�
(Y�rBB�x|��ΰ3��4�C���������߼{8ܿ}�����?������~���ǿ~���??����7����!~xw�Z�/�"����da_������û�o�����Ͽxq�8V����@cu��7�X]b���.w�4��ڍ ��;u#@�{�n�:��7B���p4�
��g��}A(�_���
\�H�@cy���	�^����[�Imf����}�� 2�$��� 4SM���Mz$()�4��6���()K�(�������է6H\����V�6�7�4`�h�a�pk^�j@|Ƅ ���	4������A�SV�Q�<a��������yGO�X���E�����bo��#��+.M�(z%��Ìt=9Q��ൡ�GI��iz�h"��U)��H:�w�LSh�0����X-�Bi����� C.�}��$�Or����vl�p� #v7�ˡ��pB���a�6ICO�����i o��pU�+�?)Z��UL{�qЅ<"��|�a��R��T%�(����Y��c�l�Ps䅖�"� �Ջ��6t#���b��f �>@�ܖw�� =�D�P%���Т�� ��J �EŰ�-�Wr �gf��?D2r�>nL�=�̄�L�XEд�D��9���U��h�:2�ڵ%�s�)�w�5ҦQ`�Rڅ�q�P�[�W��&&X3hDlm+"c�O	H=���r��u�l�����������C-��k�)a�HU�m�}�����*�څ�!IyJ��S�� SB�*�~E���j�枷��!D�s�����M	��� ���*�}1Dd����S,(��0!}Zf�|m���>!4/�9�&��1��R+A���DӃ�g�J����DӾ�1=P~�aM�{�6��9�KM��>�c�t~�����K�"Κ��l��p3w�qyھ����*:���=��ء�Kߘ�6W˗�m���%�z��EXy�L&��F3@2X4����g���b/ղ�VM��qT�L=�#@�,� P�I5T�[`G <�YB"�E!�R���r�1(���V�?�?|���?�/���� �(�+f^/�~	�"�D���qXb�f{���UŲRs<�~P��=j�7�J�7-��N=�Ҟ>��Hچ�W�15��[y��e 2�!U��,`S�y'*n���4�݈�ǫUb���|_��@GE\<�t�v?1ᜀ�y�T�G5g^�zb�����k��I���)�I�F��q�L�Ƽ[~.B��we��*m��\.X�=A`�����M�h���zVP�$>%P�F�KǇ2)���r��C�"�����m�WN�'�~��q���w�y;$��+�V�9�EU�:b�z��W�U�&�3D�X�L�����J��@k�N��܍�Q�Efb���`�i���ߏ?����ة<��{�##z�|�|���!���$?���o�6����X򏗠?"[���#"|����S&È �� ���_M�)����wda�k�]�kę#: J�p]������>&��+�.C��X-��G����EP�tk�s#� � ���.�� ��a�%s^us]�:����3�^.��F�^�=R;�!����Ԉ��R��ߤ����RQ�%���Fu]3T�d�QѸ��=�ق���4�+�xR��W�2b�� h��{g�,�v�>E�5qͿ��O���j~��@���v�F�$ic�Do�s�ٝ�� �̛O     