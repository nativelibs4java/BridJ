ifeq ($(CONFIG), debug)
CPPFLAGS  +=	-O0 -g
else
CPPFLAGS  += -O3
endif

OBJS += $(foreach X,$(UNITS),$(OBJ_DIR)/$X.o)

ifeq ($(OS), darwin)
SHARED_EXT = dylib
else
SHARED_EXT = so
endif

TARGET = $(OUT_DIR)/lib$(SHARED_LIB).$(SHARED_EXT)

CC := gcc

$(TARGET): $(OBJS)
	[[ -f $(OUT_DIR) ]] || mkdir -p $(OUT_DIR)
	${CC} -shared -o $@ $? ${LDFLAGS}

$(OBJ_DIR)/%.o: %.c
	[[ -f $(OBJ_DIR) ]] || mkdir -p $(OBJ_DIR)
	$(CC) -c $(CPPFLAGS) $< -o $@

$(OBJ_DIR)/%.o: %.cpp
	[[ -f $(OBJ_DIR) ]] || mkdir -p $(OBJ_DIR)
	$(CC) -c $(CPPFLAGS) $< -o $@

$(OBJ_DIR)/%.o: %.m
	[[ -f $(OBJ_DIR) ]] || mkdir -p $(OBJ_DIR)
	$(CC) -c $(CPPFLAGS) $< -o $@

$(OBJ_DIR)/%.o: %.S
	[[ -f $(OBJ_DIR) ]] || mkdir -p $(OBJ_DIR)
	$(CC) -c $(CPPFLAGS) $< -o $@

all: $(TARGET)
clean:
	rm -fR $(OUT_DIR) $(OBJ_DIR) $(TARGET)
